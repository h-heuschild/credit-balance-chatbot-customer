# credit-balance-chatbot-customer

Um das Projekt korrekt zu bauen muss als erstes das jar File gebaut werden:
``mvn install dependency:copy-dependencies``

Danach kann man mittels der Commandline das Jar ausführen. Aktuell gibt es zwei Modus indem die Anwendung gestartet werden kann. Bei ersten Modus wird von einem Kunden der aktuell Monatlich verfügbare und noch vorhandene Credit abgefragt:
``java -jar target/customer-credit-balance-1.0-SNAPSHOT.jar Creditabfrage 1234``

Und beim anderen Modus werden die Monatlichen vorhandenen Credits eines Kunden angepasst (Im Beispiel auf 4000):
``java -jar target/customer-credit-balance-1.0-SNAPSHOT.jar Crediterhoehung_Monat 1234 4000``


