@ECHO OFF  
CLS 
java -Dfile.encoding=cp866 -jar BroBoxServer\dist\BroBoxServer.jar 192.168.1.3 8765
pause