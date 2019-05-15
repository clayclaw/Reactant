# Swampium

Swampium core is a spigot plugin framework which provide a better way for developing spigot plugins.

Including:
 - Dependency injection
 - Service instance managing
 - Initialize order resolving
 - Defined common service interface (for example: economy, faction and configs)
 - and the utils that you need.

## Build
If you would like to compile it yourself, clone the project and ensure you have JDK 8.

After that, use `./gradlew build` (`gradlew build` for windows) to start your build.

You can find the compiled jars in `./build/libs`. 
Use `swampium-*-all.jar` which including the dependencies if you would like to load as spigot plugin.

## Quick start
### Add as dependency
For gradle:
```groovy
repositories {
    maven {
        url https://dl.bintray.com/setako/swamphut
    }
}

dependencies {
    compileOnly "net.swamphut:swampium:0.0.2-rc"
}
```
### Modify your plugin class
Firstly, create your plugin just like a normal spigot plugin, and add Swampium as depend in your `plugin.yml`.
```yaml
depend:
  - Swampium
```

Add a annotation `@SwampiumPlugin` on your plugin class, and tell swampium where to find your services.
The following config will match `net.swamphut.demo.*`.
```java
@SwampiumPlugin(servicePackages = "net.swamphut.demo")
public class DemoPlugin extends JavaPlugin {
    
}
```
If your services are in multiple packages:
```java
@SwampiumPlugin(servicePackages = {"com.otherpackage.test.demo", "net.swamphut.demo"})
```

### Your first service
We have defined a testing service interface called `HelloService`,
let's use it to say hello when our service start.
 

```java
@SwObject
@ServiceProvider
public class DemoHelloService implements LifeCycleHook {

    @Inject
    private HelloService helloService;

    @Override
    public void init() {
        helloService.sayHello("demo");
    }
}
```

### Define a service interface
Although swampium have define the most common features service interfaces, 
but you can define a new service interface as you wish.
Don't forgot to create an issues if you found that we are missing an important interface.

Now we are going to define a simple messaging interface.
Before we start working on it, we have to clearly understand what function should a messaging service have.
As an example, we will just include some of them:
 - sendMessage(Player sender, Player receiver, String message)
 - getMessageSentHistory(Player sender);

Then, we have to analyze should it be an async method, if it should, 
then let's define its return type as `Observable`/`Single`/`Completable`

```java
public interface ChattingService{
    Completable sendMessage(Player sender, Player receiver, String message);
    Single<List<String>> getMessageSentHistory(Player sender);
    //more definition...
}
```

After we defined the `ChattingService` interface,
all service which have implements and providing it will be consider as an available `ChattingService`.

### Implement an service provider
To implement an service interface, we have to use annotation `@ServiceProvider`, 
and declare which classes you are providing.

Assume that you have completed the above task, and defined a `ChattingService` interface, 
the tutorial is not going to show how to save the data, so we will not save the message here.
```java
@SwObject
@ServiceProvider(provide = ChattingService.class)
public class SimpleChattingService implements ChattingService{
    
    //Since we are not going to introduce data saving here
    private Map<Player, List<String>> sentHistories = new HashMap<>();
    
    @Override
    public Completable sendMessage(Player sender, Player receiver, String message){
        return Completable.fromAction(() -> {
            receiver.sendMessage("[" + sender + "]" + message);
            if (!sentHistories.containsKey(sender)){
                sentHistories.put(sender,new ArrayList<>());
            }
            sentHistories.get(sender).add(message);
        });
    }
    
    @Override
    public Single<List<String>> getMessageSentHistory(Player sender){
        return Single.fromCallable(() -> sentHistories.getOrDefault(sender, new ArrayList<>()));
    }
}
```

Now `SimpleChattingService` can be inject as an `ChattingService`
