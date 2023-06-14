# CGSC2023
Coding game spring challenge

## Trobule shooting

has not provided 1 lines in time
- Check that you have not used any print statements. println is ok, but print is not. (Commands needs ln)


//Kirjotetaanpa uusiksi.
//Edellisestä yrityksestä hyviä löytöjä:
// - LINE tekee huonoja valintoja jos on useampi yhtä pitkä reitti
// - Munia ei kannata kerätä loppuun asti, jos resurssit on jo muutenkin vähissä
// - ehkei kaikkia murkkuja kannata levittää vaan käyttää muutamaa lonkeroa aluksi ja lisätä kun murkkuja tulee lisää
// - Älä lankea -1 bugiin :D Muista että -1 on no neighbour

    //Uuteen versioon TODO:
    // Lisää kartta luokka
    // total crystals total
    // total eggs
    // Joku syy lopettaa munien kerääminen
    // Joku raja targeteille. Esim murkut /5
    // silloin 10 /5 = 2
    // 20/5 = 4
    // 60/5 = 12
    // esto sille ettei tuu huonoja laneja
    // lyhin reittihaku ? Pitäiskö tehä reittejä ja tallentaa ne ettei tartte aina laskee
    // Ton Linen käyttö vähän mauton. Paras tehä oma.


Mietintöjä:
Pitäiskö kerätä lopussa myös munia jotka on kristallien vieressä kauempana
Maybe should introduce path or....
Add turn counter ( 100turns loppuu)
Voittaa jos on puolet krystalleista itellä, tähän joku temppu
Älä tuhlaa vuoroa jo resuja on vähemmän ku murkkuja

Implement target value !!!!! Esim häviän aina jos siellä joku 500resurssi node

Murkku pathing ei toimi. Keksi jotenki painottaa alku ja loppu hexaa

TODO: joku laskuri joka kattoo paremmin riittääkö murkut tasaisesti krystalleille.
Vois toimia eri tavalla silloin kun ei ole enää munia kartalla (voi luottaa silloin että laskutulos ei muutu seuraavalla kierroksella)
Vai onko vaan painoarvot huonot

Nyt kun kaukaisempia kristalleja arvostetaan alussa enemmän täytyy katsoa yltääkö niihin edes.

Enemmän smoothimpi transitio. Muutamas pelis murkut menee ees taas kun arvot togglee

munilla ja kristalleilla vois olal joku suhde joka myös vaikuttaa. Esim jos on vaan pari munaa ja kauheesti krystalleja niin munat on todella arvokkaita


