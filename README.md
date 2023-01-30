# What is this?

This gradle plugin helps developers, who organise dependencies with Kotlin files in a central place such as `buildSrc`
or `includeBuild`, to check if new updates are available and add them to comments along with the dependencies therefore
they can easily upgrade without needing to search it in repositories. Simply just update your version by looking at the
updates in comments. The goal is to keep whatever you've in your Kotlin files such as property declarations, formatting
and comments, but just adding extra comments of updates.

Furthermore, if you are using Android Studio and you have `Newer Library Versions Available` turned on, in your gradle
files you can see hints for dependency updates:
![](https://i.imgur.com/6LMQX6i.png)

But if you declare dependencies variables in Kotlin files then you lose the ability:
![](https://i.imgur.com/gBlgoyN.png)

This plugin helps to solve the problem.

There are already some libraries there to address this problem, but some require you to migrate your code to use them.
It is a hassle to revert those changes if you later decide to stop using them. Some require you to run command to see
updates. For me, it is not intuitive. I want to see updates along with dependencies, I don't want to make change to my
declaration. What is why I made this plugin.

This is the quick example:\
Before

```kotlin
object Junit {
    const val junit = "junit:junit:4.12"
}
```

After

```kotlin
import io.github.zeroarst.dependencyupdatescommenter.CommentUpdates

object Junit {
    // Available versions:
    // 4.13-rc-2
    // 4.13-rc-1
    // 4.13-beta-3
    // 4.13-beta-2
    // 4.13-beta-1
    @CommentUpdates
    const val junit = "junit:junit:4.12"
}
```

---

# What should I be aware of before I use it?

* Under the hood, the plugin uses Regular Expression to parse the Kotlin file content to find dependency coordinate and
  query repositories for updates. Because of that, it is not 100% reliable. It relies on the format of the Kotlin files.
  You might ask why don't use KSP or AST parser? Please refer to [Verbosity](#Verbosity).

* Line breaks are supported:
  * ✅CRLF  - Windows (\r\n)
  * ✅LF - macOS and Unix (\n)
* Line break is supported:
  * ❌CR - Classic Mac OS (\r) 
* It only searches `search.maven.org` and `maven.google.com` repositories at the moment. If there are other repositories you want to support please let me know.
* This is my first Gradle plugin so be gentle. I am still learning and welcome any feedback! 😊

---

# Install

Make sure you have gradlePluginPortal repository in `Setting.gradle` or `Setting.gradle.kts`.

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
```

### Apply the plugin
Kotlin DSL
```kotlin
plugins {
    id("io.zeroarst.github.dependency-updates-commenter")
}
```

Groovy

```groovy
plugins {
    id 'io.zeroarst.github.dependency-updates-commenter'
}
```

### Add annotation `@CommentUpdates` to properties in Kotlin files.

```kotlin
import io.github.zeroarst.dependencyupdatescommenter.CommentUpdates

object Junit {
    @CommentUpdates
    const val junit = "junit:junit:4.12"
}
```
If you have a group of dependencies that use the same version, for example, `androidx.activity:activity`
and `androidx.activity:activity-ktx`. You can specify dependency coordinate along with annotation.

```kotlin
import io.github.zeroarst.dependencyupdatescommenter.CommentUpdates

object AndroidX {
    @CommentUpdates(coordinate = "androidx.activity:activity")
    const val activityVersion = "1.6.1"
    const val activity = "androidx.activity:activity:$activityVersion"
    const val activityKtx = "androidx.activity:activity-ktx:$activityVersion"
}
```

### Execute the task
#### Via IDE
If you are using JetBrains IDEs, you can find the gradle task in Gradle panel. The task is `commentDependencyUpdates` under `dependenc update commenter`. Double click can execute it.\
![](https://i.imgur.com/JN60A2C.png)

Note if you apply the plugin to `buildSrc`, you might not able to see the task. [This link](https://discuss.gradle.org/t/is-it-possible-to-create-a-task-in-buildsrc/44753/2?u=zeroarst) says it is because Gradle does not deliver the `buildSrc` tasks to JetBrains IDEs. Please use command line instead: `./gradlew -p buildSrc cDU` or `./gradlew -p buildSrc commentDependencyUpdates`. [With the fix in Gradle 8.0](https://github.com/gradle/gradle/pull/22540), you might be able to run `./gradlew :buildSrc:cDU`.

[Composite build](https://docs.gradle.org/current/userguide/composite_builds.html#composite_build_intro) works fine in this case.


#### Via Command Line
You could also run the task it via command line `./gradlew :module-if-have:cDU`
or `./gradlew :module-if-have:commentDependencyUpdates` with [Options](#options). Note this only works on gradle version 7.6.

Again, if apply this plugin to `buildSrc`, `./gradlew :buildSrc:cDU` will not work. Check the [explanation above](#via-ide).


### Results
Once executed, wait for the task completed, then you will get:

```kotlin
import io.github.zeroarst.dependencyupdatescommenter.CommentUpdates

object Junit {
    // Available versions:
    // 4.13-rc-2
    // 4.13-rc-1
    // 4.13-beta-3
    // 4.13-beta-2
    // 4.13-beta-1
    @CommentUpdates
    const val junit = "junit:junit:4.12"
}
```
```kotlin
import io.github.zeroarst.dependencyupdatescommenter.CommentUpdates

object AndroidX {
    // Available versions:
    // 1.6.0
    // 1.6.1
    // 1.7.0-alpha01
    // 1.7.0-alpha02
    // 1.7.0-alpha03
    @CommentUpdates("androidx.activity:activity")
    const val activityVersion = "1.6.1"
    const val activity = "androidx.activity:activity:$activityVersion"
    const val activityKtx = "androidx.activity:activity-ktx:$activityVersion"
}
```

---

# Supported Format

Currently only below property declarations are supported.

```kotlin
import io.github.zeroarst.dependencyupdatescommenter.CommentUpdates

Object Junit {

    // read-only property.
    @CommentUpdates
    val junit = "junit:junit:4.12"

    // compile-time constants.
    @CommentUpdates
    const val junit = "junit:junit:4.12"

    // lazy property.
    @CommentUpdates
    val composeUI: String by lazy { "junit:junit:4.12" }

    // lazy property with multiple lines.
    @CommentUpdates
    val composeUI: String by lazy {
        "junit:junit:4.12"
    }
}

// nested object
object AndroidX {
    object Compose {
        @CommentUpdates
        const val animation = "androidx.compose.animation:animation:1.3.0"
    }
}


```

---

# Configuration

In the project's `build.gradle.kts` or `build.gradle`, you can configure options like this:

```kotlin
dependencyUpdatesCommenter {
    scanPath = ""
    scanSubDirectories = false
    order = Order.LATEST_AT_BOTTOM
    onlyReleaseVersion = false
    maximumVersionCount = 5
    usingLatestVerComment = "You are using the latest version."
    generateNewFile = true
}
```

If you use command line, run `./gradlew help --task :module-if-have:cDU` will show you all options. You can execute the
task with options like
this: `./gradlew task --help :module-if-have:cDU --maximumVersionCount=5 --onlyReleaseVersion=true`

---

# Options

### scanPath: String

default: ""\
If not specified (empty or null), plugin looks up the first available source dir.
Typically `yourProject/src/main/kotlin` or `yourProject/src/main/java`.

### scanSubDirectories: Boolean

default: false\
Whether to process Kotlin files in sub dirs.

### order: LATEST_AT_BOTTOM or LATEST_AT_TOP

default: LATEST_AT_BOTTOM\
Ex.\
`order = LATEST_AT_BOTTOM`\
![](https://i.imgur.com/rqZdrKi.png)\
`order = LATEST_AT_TOP`\
![](https://i.imgur.com/XcENdrm.png)

### onlyReleaseVersion: Boolean

default: false\
Ignore all updates that have qualifier. Ex. beta, alpha, RC...etc.\
Ex. `onlyReleaseVersion = true`\
![](https://i.imgur.com/HMe3hi4.png)

### maximumVersionCount: Int

default: 20\
How many new version items to be added. Using 1 just get the latest version. Combine with `onlyReleaseVersion = true`
only shows the latest release version.\
Ex. maximumVersionCount = 1\
![](https://i.imgur.com/lWCrgFg.png)

### usingLatestVerComment: String

default: "State of the art! You are using the latest version."\
What to put in the comment if using the latest version\
Ex. `usingLatestVerComment = "You are using the latest version."`\
![](https://i.imgur.com/rZOzlEl.png)

### generateNewFile: Boolean

default: false\
Whether generate new file for the result with suffix "2". Useful if you want to see the result instead of overwriting
the content.\
Ex. `generateNewFile = true`\
![](https://i.imgur.com/HpnaEf1.png)

---

# Verbosity

<p>
Using Regex as the core of the plugin parser was not my first intention. Before I decided to use implement this via Regex, I did some research and tried different things to find out what is the best way to do it. 
</p>
<p>
The first thing I tried is Kotlin KSP, which is not applicable because it can only generate files in build folder, although it is the best way to process annotations.
</p>
<p>
Second thing I tried is Kotlin parsers. There are some libraries that parse the Kotlin file into AST (Abstract Syntax Tree) then allow you to manipulate the tree, write back to the file. The one that I got the closest result is: <a href="https://github.com/cretz/kastree">certz's kastree</a>. However, it is not 100% perfect because it changes the original file content unexpectedly such as removing line breaks.
</p>
<p>
There are some other tools that I investigated :
https://github.com/square/kotlinpoet
</p>

<p>
Nevertheless, there might be something that I missed or don't know that actually work better than using Regular Expression. If you know any please let me know. 
</p>

# Report Issues
If you come across any issue please report it with the log printed via command line: `./gradlew :module-if-have:cDU --debug | grep duc-log`. For `buildSrc` run `./gradlew -p buildSrc cDU --debug | grep duc-log`. 