import dev.reactant.rui.ReactantRui
import dev.reactant.rui.render.Update
import dev.reactant.rui.render.ruiRenderGlobalState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

fun <T> claimState(initialStateFactory: () -> T): Pair<() -> T, Int> = hookGuard {
    if (states.size == this.usedStateCount) states.add(initialStateFactory())

    val usingStateIndex = this.usedStateCount++

    @Suppress("UNCHECKED_CAST")
    val valueGetter = {
        try {
            states[usingStateIndex] as T
        } catch (e: ClassCastException) {
            throw IllegalStateException("State type mismatch: found ${states[usingStateIndex]?.javaClass}, probably because of unstable use hook")
        }
    }
    valueGetter to usingStateIndex
}

fun updateState(update: Update) {
    update.targetNodeState.rootUI?.let { rootUI ->
        ruiRenderGlobalState.let { ruiState ->
            // if no pending mean it must not start handling event, cannot do batch update!
            if (ruiState?.pendingBatchUpdate == null) {
                // add itself to update object
                rootUI.batchedUpdates.onNext(listOf(update))
            } else {
                // batch them
                ruiState.pendingBatchUpdate!! += update
            }
        }
    }
}

fun <T> useState(initialState: T): Pair<T, (T) -> Unit> = hookGuard {
    val (valueGetter, stateIndex) = claimState { initialState }

    // also memo the setter, avoid setter reference change
    val (valueSetterGetter) = claimState { { nextState: T -> updateState(Update({ it[stateIndex] = nextState }, this)) } }
    (valueGetter() to valueSetterGetter())
}

fun <T> useMemo(factory: () -> T, deps: Array<Any>? = null) = hookGuard {
    val (originalDepsGetter, originalDepsStateIndex) = claimState { deps }
    val (memoValueGetter, memoValueStateIndex) = claimState(factory)
    val originalDeps = originalDepsGetter()

    if (deps == null || originalDeps == null || deps.size != originalDeps.size || deps.zip(originalDeps).any { (a, b) -> a != b }) {
        this.states[memoValueStateIndex] = factory()
        this.states[originalDepsStateIndex] = deps
    }

    memoValueGetter()
}

fun useEffect(effect: () -> (() -> Unit)?, deps: Array<Any>? = null) = hookGuard {
    val (originalDepsGetter, originalDepsStateIndex) = claimState<Array<Any>?> { null }
    val unmountActionRef = useRef<(() -> Unit)?>(null)
    val originalDeps = originalDepsGetter()

    if (deps == null || originalDeps == null || deps.size != originalDeps.size || deps.zip(originalDeps).any { (a, b) -> a != b }) {
        unmountActionRef.current?.invoke() // dispose last effect
        this.states[originalDepsStateIndex] = deps
        this.domRenderedAction.add {
            effect()?.let {
                ReactantRui.logger.info("Added:")
                unmountActionRef.current = it
                this.unmountActions.add(it)
            }
        }
    }
}

fun <T : () -> Any?> useCallback(callback: T, deps: Array<Any>? = null) = useMemo({ callback }, deps)

data class RuiRef<T>(var current: T)

fun <T> useRef(initialValue: T) = hookGuard {
    val (valueGetter, valueStateIndex) = claimState { RuiRef(initialValue) }
    valueGetter()
}

fun <T : Any?> useObservable(subject: BehaviorSubject<T>): T? = useObservable(subject, subject.value)

fun <T : Any?> useObservable(observable: Observable<T>): T? {
    val (lastValue, setLastValue) = useState<T?>(null)
    useEffect({
        observable.subscribe { setLastValue(it) }.let { { it.dispose() } }
    }, arrayOf(observable, setLastValue))
    return lastValue
}

fun <T : Any?> useObservable(observable: Observable<T>, initialState: T): T {
    val (lastValue, setLastValue) = useState(initialState)
    ReactantRui.logger.info("DEB=2===")
    useEffect({
        ReactantRui.logger.info("DEB=====")
        observable.subscribe {
            ReactantRui.logger.info("DEB" + it)
            setLastValue(it)
        }.let { { it.dispose() } }
    }, arrayOf(observable, setLastValue))
    return lastValue
}
