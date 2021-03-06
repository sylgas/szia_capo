# Systemy zdecentralizowane i agentowe - CAPO

## Instalacja

```
git clone https://github.com/sylgas/szia_capo.git
cd szia_capo\szia_capo_robot\lib
make.exe --file=Makefile.win
cd ..\..\
mvn clean install
```

Wersja Apache Maven: 3.0.5 

## Uruchomienie symulatora

```
java -jar szia_capo_simulation\target\szia-capo-simulation-1.0-SNAPSHOT.jar
```

## Wizja

Celem projektu jest stworzenie modułu pozwalającego na określenie lokalizacji robota mobilnego na podstawie odczytów z sensorów. Robot może poruszać się po określonych pomieszczeniach. Jako dane wejściowe będzie przyjmowana lista ścian, przejść oraz pokoi (w formacie JSON). Cyklicznie będą podawane odczyty z sensorów robota (co ok. 200 ms) tj. prędkości kół (prawego i lewego), odległość od przeszkód w polu "widzenia" robota (ok. 240 stopni). Analizowane będą pomiary co 8 stopni. 

W każdym z pomieszczeń będzie znajdował się agent, określający prawdopobieństwo wystąpienia robota w wybranych punktach tego pokoju. Pozycje agenta będą wybierane na podstawie podanych pomiarów i charakterystyki pomieszczenia, w którym się znajduje. Agent będzie symulował ruchy robota i porównywał odczyty z sensorów z własną pozycją. Działania agentów będą koordynowane przez "planistę" (schedulera), który na podstawie prawdopodobieństwa obliczonego przez każdego z nich będzie przydzielał czas obliczeń w kolejnej iteracji (najdłuższy dla agentów dających najbardziej obiecujące wyniki).

Mapa pomieszczeń, po których przemieszcza się robot oraz działania każdego z agentów będą na bieżąco wizualizowane. 

Zadanie zostanie zrealizowane w języku Java poza obliczeniami, które będą w języku C. Ze względu na ograniczone zasoby program będzie działał w dwóch wątkach - jeden będzie przeznaczony dla schedulera, a drugi dla agentów.
