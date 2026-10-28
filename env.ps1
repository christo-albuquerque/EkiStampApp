# Dot-source: . .\env.ps1  — reuses the portable JDK/SDK from the TV-TIME project (current shell only).
$tools = 'D:\Projects\Tools'
$env:JAVA_HOME        = "$tools\jdk17"
$env:ANDROID_HOME     = "$tools\android-sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:GRADLE_USER_HOME = Join-Path $PSScriptRoot '.gradle-home'
$env:PATH = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:PATH"
$env:PATH = "D:\Projects\Tools\git\cmd;D:\Projects\Tools\gh\bin;$env:PATH"
