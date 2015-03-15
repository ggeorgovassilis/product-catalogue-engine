The Product Catalogue Engine is an attempt to create an independent component that can manage and model products and product releated business rules of any kind.

**Features** :

  * Read products and rules from Excel files
  * Programmatic access to products from Java applications
  * Provide your own domain specific language (DSL) in Javascript
  * Write business rules in plain English

**Examples** of real rules :

_When_

`    Type is 'Mobile Phone Offer' and Name starts with 'XMAS' and ValidFrom After '01/01/2012'`

_Then_

`    discount Price by 10`


---


_When_

`    ID is 'Bundle2'`

_Then_

`    set Price to price of [CaseID]`

`    increase Price by price of [LaptopID]`

`    discount Price by 10%`

`    abort`

![http://4.bp.blogspot.com/-DKzOOWCROoQ/TqZKVKiio2I/AAAAAAAAAFc/pWsUssfGTQY/s640/PCE-phones.png](http://4.bp.blogspot.com/-DKzOOWCROoQ/TqZKVKiio2I/AAAAAAAAAFc/pWsUssfGTQY/s640/PCE-phones.png)

![http://2.bp.blogspot.com/-gDEQK6Hm2Oc/TqZKXym7e7I/AAAAAAAAAFk/GgWoSGlJ0sc/s640/PCE-rules.png](http://2.bp.blogspot.com/-gDEQK6Hm2Oc/TqZKXym7e7I/AAAAAAAAAFk/GgWoSGlJ0sc/s640/PCE-rules.png)