# TelnetHelper

![jenkins](https://img.shields.io/badge/Jenkins-What%20is%20that-red) 
![build](https://img.shields.io/badge/Build-dying-red) 
![code contains potatos](https://img.shields.io/badge/Code%20contains-potatos-blue)

## What is this project about?
Let's say this is an addon to
[Igor725's Telnet Battleship game](https://github.com/igor725/telebattle) (clickable link
to their GitHub repository) which implements counting and limiting the number of active 
connections... being a proxy (tunnel) between clients and the origin server.
## Is this a joke?
Maybe. At least I've finally implemented something similar to Non-blocking IO from
scratch. It did not look that hard though, just always wanted to try this out at some point.
## What if I say that the online player counter was introduced in [1e60076](https://github.com/igor725/telebattle/commit/1e60076b8515fe82aaedd6de3af4813a9d99a44d)?
If you continue asking such questions & being against my infrastructure solutions, you will
be BANNED on my super secret Minecraft: Java Edition server. Just tell me your IP, so I can
blacklist it.
## Building
Step 1. Ensure you have Java 8 installed.

Step 2. Download the project.

Step 3. Import it into IntelliJ IDEA.

Step 4. File -> Project Structure -> Artifacts -> Add -> JAR -> From modules with dependencies...

Step 5. Select the main class to `ru.deewend.telnethelper.Main`, press OK.

Step 6. Press Apply.

Step 7. Build -> Build Artifacts... -> TelnetHelper:jar -> Build.

Step 8. Your jarfile will be located in the `out/artifacts/TelnetHelper_jar` folder, enjoy! <3