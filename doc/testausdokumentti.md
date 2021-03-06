# Testausdokumentti

Järjestelmän testit muodostuvat kolmesta kokonaisuudesta:
yksikkötesteistä, integraatiotesteistä sekä suorituskykytesteistä.

Yksikkötestit testaavat yksittäisten funktioiden arvoja eri syötteillä,
integraatiotestit testaavat järjestelmän toimintaa kokonaisuutena ja
suorituskykytestit mittaavat järjestelmän suorituskykyä erilaisilla syötteillä.

Yksikkö- ja integraatiotestit on toteutettu [ScalaTest][1]-testauskirjastolla. Lisäksi
ne tietorakenteet, jotka muodostavat jonkin algebrallisen rakenteen, sisältävät
[ScalaCheck][2]-kirjastolla toteutetut aksioomatestit. Nämä testit on pitkälti toimitettu `cats`-kirjastossa, eikä
niitä ole kirjoitettu itse. Testikattavuusraportti generoidaan IntelliJ:n sisäänrakennetulla
kattavuustyökalulla ja se sisältää kattavuustiedon sekä integraatio- että yksikkötesteistä.

Testikattavuusraportti kertoo virheellisen prosenttiluvun testien kattavuudesta, sillä osa koodista on erikoistettu JVM:n primitiivityypeille "boxingin" välttämiseksi. Järkevämpää onkin siis tarkastella yksittäisten luokkien kattavuusprosentteja koko järjestelmän kattavuusprosentin sijaan.

## Testien suorittaminen

Testit voidaan suorittaa komentoriviltä komennolla `sbt test`. Suorituskykytestien suorittamiseen vaadittavat komennot löytyvät testiluokkien kommenteista.

## Algoritmin hyötysuhde

Olkoon $f: \Sigma^* \to \Sigma^*$ funktio, joka pakkaa annetun merkkijonon LZW-pakkauksella. Nyt algoritmin pakkaussuhde voidaan ilmaista funktiona

$$r(S) = \frac{|S|}{|f(S)|}$$

Tarkastellaan algoritmin pakkaussuhdetta testikorpuksen eri syötteillä.

Tiedosto | Alkuperäinen koko (b) | Pakattu koko (b) | Pakkaussuhde
--- | --- | --- | ---
`fixtures/alice29.txt` | 152089 | 62247 | 2,44
`fixtures/ptt5` | 513216 | 62215 | 8,24
`fixtures/xargs.1` | 4227 | 2339 | 1,8
`fixtures/kennedy.xls` | 1029744 | 320789 | 3,21

Pakkaussuhteessa ei ole selkeää korrelaatiota syötteen koon kanssa, vaan LZW-pakkauksen tehokkuus riipuu siitä, kuinka paljon toistuvia osajonoja syötteessä on; Welchin omien empiiristen tulosten perusteella LZW-algoritmi pakkaa englanninkielistä tekstiä pakkaussuhteella 1.8, joten tämä toteutus pakkaavan tietoa yhtä tehokkaasti. [Welch1]

## Suorituskykytestit

### `CompressionBenchmarks`

Nämä suorituskykytestit mittaavat koko järjestelmän suorituskykyä makrotasolla.

Tuloksissa yksikkö `op` tarkoittaa yhtä operaation suoritusta, mikä toisin sanoen
vastaa pakkausta tai purkua annetulla syötteellä. Näin ollen nopeus $\frac{MB}{s}$ voidaan johtaa kaavalla

$$v = \frac{n}{t}$$

jossa $t$ on yhden operaation viemä aika sekunneissa ja $n$ on operaatiolle annetun syötteen
koko.

## Suorituskykytulokset

Suorituskykytestit on suoritettu seuraavalla laitteistolla:
Apple MacBook Pro (2016); Intel i7 3,3GHz, 16GB DDR3, SSD.

---

#### Tulokset 9.3.

| Testi | Syötteen koko (MB) | Nopeus (s/op), n = 10 | Nopeus (MB/s) |
| ----- | ------------- | --------------------- | ------------- |
| `compressAlice29` | 0,149 | 1,122 ± 0,186 (16%) | ~0,132 |
| `decompressAlice29` | 0,061 | 0,625 ± 0,039 (6%) | ~0,097 |
| `compressPtt5` | 0,501 | 4,687 ± 0,113 (2,5%) | ~0,106 |
| `decompressPtt5` | 0,061 | 0,784 ± 0,334 (42%) | ~0,077 |

`compressAlice29`- ja `decompressPtt5`-testeillä on tässä mittauksessa korkea varianssi, mutta ottamalla huomioon edellisen testien samat tulokset pienemmillä variansseilla voidaan tulosten olettaa olevan tarpeeksi luotettavia.

Tulokset graafisessa muodossa:

![Tulokset](benchmarks.png)

#### Tulokset 3.3.

| Testi | Syötteen koko (MB) | Nopeus (s/op), n = 10 | Nopeus (MB/s) |
| ----- | ------------- | --------------------- | ------------- |
| `compressAlice29` | 0,149 | 1,110 ± 0,091 (8%) | ~0,134 |
| `decompressAlice29` | 0,061 | 0,718 ± 0,063 (9%) | ~0,084 |
| `compressPtt5` | 0,501 | 4,773 ± 0,123 (2,5%) | ~0,104 |
| `decompressPtt5` | 0,061 | 0,706 ± 0,065 (9%) | ~0,086 |

Vaikuttaa siltä, että omien tietorakenteiden käyttö on heikentänyt suorituskykyä jonkin verran erityisesti pakkauksen
osalta. Pakkauksen ja purkamisen tehokkuussuhde on nyt paljon pienempi, noin 1:1.3. Vaikuttaa siis siltä, että omat
tietorakenteet ovat hidastaneet pakkausta enemmän.

---

### Tulokset 7.2.

| Testi | Syötteen koko (MB) | Nopeus (s/op), n = 10 | Nopeus (MB/s) |
| ----- | ------------- | --------------------- | ------------- |
| `compressAlice29` | 0,149 | 0,613 ± 0,061 | ~0,243 |
| `decompressAlice29` | 0,061 | 0,604 ± 0,264 | ~0,100 |
| `compressPtt5` | 0,501 | 1,880 ± 0,264 | ~0,266 |
| `decompressPtt5` | 0,061 | 0,598 ± 0,089 | ~0,102 |

Tällä hetkellä suorituskyky näyttäisi olevan hyvin heikkoa moneen "oikeaan"
pakkausalgoritmiin verrattuna. Toisaalta pakkausnopeudessa ei vaikuta olevan eroa
syötteen kokoon nähden, joten empiirisesti tarkasteltuna O(n) aikavaativuus voisi toteutua sekä pakkaukselle että purkamiselle.

Pakkauksen ja purkamisen tehokkuussuhde on noin 1:2.5.

[1]: http://www.scalatest.org/user_guide
[2]: https://www.scalacheck.org/