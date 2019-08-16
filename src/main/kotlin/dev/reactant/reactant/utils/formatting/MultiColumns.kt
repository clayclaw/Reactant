package dev.reactant.reactant.utils.formatting

class MultiColumns {
    val colSettings: ArrayList<ColumnSetting> = arrayListOf()

    val rows: ArrayList<List<String>> = arrayListOf()

    enum class Alignment {
        Left, Center, Right
    }

    class ColumnSetting {
        var minLength: Int = 0
        var maxLength: Int = 20
        var overflowCutFromRight: Boolean = true
        var overflowRepresenting: String = "..."
        var leftMargin: Int = 1
        var rightMargin: Int = 1
        var align: Alignment = Alignment.Left
    }

    fun generate(): List<String> {
        val colLengths: MutableList<Int> = colSettings.map { it.minLength }.toMutableList()

        if (colSettings.filter { it.overflowRepresenting.length > it.maxLength }.isNotEmpty())
            throw IllegalStateException("Overflow representing text must be shorter than max col length")

        rows.forEach { row ->
            if (row.size != colSettings.size) {
                throw IllegalArgumentException(
                        "Column amount not match, Expected: ${colSettings.size}, Actual: ${row.size}")
            }
            row.forEachIndexed { colIndex, col ->
                if (colLengths[colIndex] < col.length) {
                    if (colSettings[colIndex].maxLength > col.length) colLengths[colIndex] = col.length
                    else colLengths[colIndex] = colSettings[colIndex].maxLength
                }
            }
        }

        val resultRows: ArrayList<String> = arrayListOf();

        rows.forEachIndexed { rowIndex, row ->
            resultRows.add("")
            row.forEachIndexed { colIndex, colText ->
                val length = colLengths[colIndex]
                val colSetting = colSettings[colIndex]

                var finalText = colText;
                if (colText.length > length) {
                    finalText = (length - colSetting.overflowRepresenting.length).let {
                        if (colSetting.overflowCutFromRight)
                            colText.take(it) + colSetting.overflowRepresenting
                        else
                            colSetting.overflowRepresenting + colText.takeLast(it)
                    }
                }

                finalText = alignFormat(finalText, length, colSetting.align)

                resultRows[rowIndex] = resultRows[rowIndex] +
                        " ".repeat(colSetting.leftMargin) +
                        finalText +
                        " ".repeat(colSetting.rightMargin)
            }
        }
        return resultRows
    }

    private fun alignFormat(str: String, length: Int, alignment: Alignment): String {
        var blankBefore = 0;
        var blankAfter = 0;
        when (alignment) {
            Alignment.Left -> blankAfter = length - str.length
            Alignment.Center -> (length - str.length).let { remainSpace ->
                blankBefore = Math.floor(remainSpace / 2.0).toInt()
                blankAfter = Math.ceil(remainSpace / 2.0).toInt()
            }
            Alignment.Right -> blankBefore = length - str.length
        }
        return " ".repeat(blankBefore) + str + " ".repeat(blankAfter);
    }

    class MultiColumnsCreation internal constructor() {

        val multiColumns: MultiColumns = MultiColumns()
        fun column(config: (ColumnSetting.() -> Unit)? = null) {
            multiColumns.colSettings.add(ColumnSetting().apply { config?.let { setting -> this.setting() } })
        }
    }

    companion object {
        fun create(creation: MultiColumnsCreation.() -> Unit): MultiColumns {
            return MultiColumnsCreation().apply(creation).multiColumns
        }
    }
}
