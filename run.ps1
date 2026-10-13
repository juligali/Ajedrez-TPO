param([switch]$Test, [switch]$Console, [int]$Port = 8080)
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($mavenCommand) {
    $mavenPath = $mavenCommand.Source
} else {
    # Permite reutilizar Maven descargado previamente por el IDE.
    $distributionRoot = Join-Path $env:USERPROFILE '.m2\wrapper\dists'
    $distribution = Get-ChildItem -LiteralPath $distributionRoot -Filter mvn.cmd -Recurse -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending | Select-Object -First 1
    if (-not $distribution) { throw 'Se necesita Maven. Instalalo y agrega su carpeta bin al PATH, o configura Maven en el IDE.' }
    $mavenPath = $distribution.FullName
}
if ($Test) {
    & $mavenPath -B -ntp test
    exit $LASTEXITCODE
}
& $mavenPath -B -ntp compile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    $javaPath = Join-Path $env:JAVA_HOME 'bin\java.exe'
} else {
    $javaPath = (Get-Command java -ErrorAction Stop).Source
}
if ($Console) { & $javaPath -cp target/classes ar.edu.ajedrez.app.ConsoleUI }
else { & $javaPath -cp target/classes ar.edu.ajedrez.app.WebServer $Port }
exit $LASTEXITCODE

