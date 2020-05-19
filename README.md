# POC DDD Direct Debit mandate
![Test](https://github.com/abaddon/POC_DDD_ddmandate/workflows/Test/badge.svg)
## Why this POC

The scope of this POC is to implement a service based on:

 - [EventStorming](https://www.eventstorming.com/)  to help me to discover the domain and then define the logic on the paper
 - [Domain Driven Design](https://martinfowler.com/tags/domain%20driven%20design.html), it's the approach that I want to follow to manage the business logic and its information 
 - [Hexagonal_architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) an architectural pattern to organise all the application components. This architecture put the Domain model in the center of the application an it works pretty well with a DDD approach.

The main goals that I want to achive are:

 1. Create an application well organised with a package structure easily to navigate
 2. Mantain each aggregate completly indipendent from the others
 3. Be able to separate the aggregates managed in the same app in 2  or more app one for aggregate for example with a very limited effort and touching only the most external part.


## Step 1. Domain discovery  

The domain that I decided to analyse is the creation of a Direct Debit in the financial word.
Few thigs to know regarding this domain:
1. SEPA and Bacs doesn't have any kind of protocol that evaluate if the customer is subscribing a DD Mandate using his banks or not... this check is in charge to the firm
2. An investment firm can accept investment only from customer's bank accounts. It's not possible accept money that come from a parent's bank account or other

Su

to manage the subscription of a  Direct Debit mandate.

 using the [Domain Driven Design](https://martinfowler.com/tags/domain%20driven%20design.html)  approach and the [Hexagonal_architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)).

The service lives in the Legel Context, so this service doesn't manage any payment logic but implement only the business rules needed to manage the mandate as agreement between the Debitor and the Debitor.

To design the service I used the [EventStorming](https://www.eventstorming.com/) methodology. The outcome of the ES design is visible [here](#event-storming-design).

The Context map below should helps to contestualise the project and the parts not touched.
![Context Map](https://raw.githubusercontent.com/abaddon/POC_DDD_ddmandate/master/docs/ContextsMap.jpg)

Aggregates: 
- DD Mandate
- Contract

## Hexagonal architecture
![Hexagonal architecture](./docs/HexagonalArchitecture.jpg)

## Event Storming Design

![EventStorming - The picture that explains everything!](./docs/EventStormingDesignLegend.jpg)
![EventStorming - Design](./docs/EventStormingDesign.jpg)
### Event Storming - Aggregate view
![EventStorming - Aggregates](./docs/AggregateDefinition.jpg)

## Domain model
![Domain Model](./docs/DomainModel.jpg)
<!--stackedit_data:
eyJoaXN0b3J5IjpbNzYzNjY5MDYyLDE4MzgzNjQyNSw1MTg2MD
YxOTYsLTE0ODA3NjA1NTBdfQ==
-->