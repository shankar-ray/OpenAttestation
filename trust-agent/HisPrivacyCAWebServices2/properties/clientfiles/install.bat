rem DO NOT EDIT THIS FILE!
rem This file is generated by the Privacy CA installation utility in Java
call UninstallUSW.bat
HIS-Standalone-Setup-v3.0a.exe /VERYSILENT /SUPPRESSMSGBOXES /LOG="tpminstall.log" /DIR="C:/Program Files/NIARL/HIS/"
copy /Y OAT.properties "C:\Program Files\NIARL\HIS\HIS.properties"
copy /Y trustStore.jks "C:\Program Files\NIARL\HIS\"
copy /Y NIARL_TPM_Module.exe "C:\Program Files\NIARL\HIS\"
rem cd "HIS Provisioner" 
call provisioner.bat
cd "C:\Program Files\NIARL\HIS\service\"
call "replaceUSW.bat"