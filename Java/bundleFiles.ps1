# Might need to run following in powershell as admin before script works
# powershell Set-ExecutionPolicy RemoteSigned

$files = Get-ChildItem -Recurse -Include *.java $PSScriptRoot\src\
$output = "orangebot.java"

if(Test-Path $PSScriptRoot\$output){

    Remove-Item $PSScriptRoot\$output
    Add-Content $PSScriptRoot\$output `
    -Value "//Bundle uploaded at $(Get-Date)`nimport java.util.*;`nimport java.util.stream.Collectors;"
}

foreach ($f in $files){

    (Get-Content $f) | ForEach-Object{
        $_ -creplace 'public enum', 'enum' `
           -creplace 'public class', 'class' `
    } |
            Where-Object {$_ -notmatch 'package' -and $_ -notmatch 'import' -and $_ -ne ""} |
            Add-Content $PSScriptRoot\$output
}