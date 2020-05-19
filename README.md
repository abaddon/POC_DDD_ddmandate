# POC DDD Direct Debit mandate
![Test](https://github.com/abaddon/POC_DDD_ddmandate/workflows/Test/badge.svg)
## Why this POC

The scope of this POC is to implement a service based on:

 - [EventStorming](https://www.eventstorming.com/)  to help me to discover the domain, 
 - [Domain Driven Design](https://martinfowler.com/tags/domain%20driven%20design.html) as methodology to organise the domain model
 - [Hexagonal_architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) as architectural pattern to organise all the components of the services

The main goals that I want to achive are:

 1. Create an application well organised with a package structure easily to navigate
 2. Mantain each aggregate completly indipendent from the others
 3. Be able to separate the aggregates managed in the same app in 2  or more app one for aggregate for example with a very limited effort and touching only the most external part.

## Why this POC

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
eyJoaXN0b3J5IjpbMTcyMzcwNzI2MiwxODM4MzY0MjUsNTE4Nj
A2MTk2LC0xNDgwNzYwNTUwXX0=
-->