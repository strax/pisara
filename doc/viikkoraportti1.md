# Viikkoraportti 1

Tällä viikolla olen valinnut toteuttamani projektin aiheen, etsinyt aiheeseen liittyviä papereita ja tehnyt lähinnä tutkimustyötä. Tämän pohjalta kirjoitin määrittelydokumentin, mitä tehdessäni koin jo omaavani hyvän käsityksen siitä, miten LZW toimii ja minkälaisia tietorakenteita sen toteuttamiseen kannattaa käyttää. Opin myös trie-rakenteiden sekä juuripuiden (radix tree) toimintamallin. Bonuksena opin myös paljon Latexin käytöstä. Itse ohjelmointityötä en ole aloittanut, mutta se on tarkoitus tehdä seuraavaksi ongelmanmäärittelyn valmistuttua.

Epäselväksi määrittelydokumenttia tehdessä jäi se, miten LZW:n tilavaativuuden pahimman tapauksen voisi rajata ja todistaa. Lineaarinen tilavaatimuus tuntuu luontevalta, jos pisin mahdollinen osamerkkijoukko on maksimissaan itse merkkijonon pituinen, ja siten sen vaatima tila triessä olisi juurikin pituudensa verran. Navarron paperissa mainittiin kuitenkin jotain jopa logaritmisesta tilavaativuudesta, joten aihetta pitää selvittää lisää. Lisäksi en ehtinyt juurikaan perehtyä LZW:n purkamisosuuteen ja se jäi mielestäni liian vähälle käsittelylle määrittelydokumentissa. Koen, että on konseptuaalisesti vielä hieman epäselvää, miten sama koodisto voidaan muodostaa purkuvaiheessa.

Ensi viikon tavoitteena on siis sisäistää LZW:n purkamisen mekanismi ja aloittaa itse sovelluksen toteuttaminen, korvaten trien todennäköisesti Scalan omalla hajautustaululla aluksi.