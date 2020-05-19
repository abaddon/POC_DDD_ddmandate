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
Few thigs to know regarding the payments in the financial world:
1. SEPA and Bacs doesn't have any kind of protocol that evaluate if the customer is subscribing a DD Mandate using his banks or not... this check is in charge to the firm
2. An investment firm can accept investment only from the customer's bank accounts. It's not possible accept money that come from a parent's bank account or other

The first stem is discover the domain and the EventStorming - Big Picture is the perfect tool for this phase.
One of the possible outcome of thr Big Picture is the Context map. 
The context map is a map that explain which contexts are involved in your process, their responsability and how they communicate.
![Context Map](https://raw.githubusercontent.com/abaddon/POC_DDD_ddmandate/master/docs/ContextsMap.jpg)
To manage a DD Payment there are at least 4 context involved:
### Legal Context
Here we have manage all the rules (business logic) related to a DD mandate subscription like:
- The DD Mandate contract is created correclty? is it signed?
- The DD Mandate is associated to a valid Debtor?
- The DD Mandate could be accepted or cancelled?
- etc..
### Regulation Context
As we said previously the financial regulator ask to the firm to validate the bank account before accept the money. The financial firms often doesn't have a bank licence and then they can't accept money that come from bank accounts where the owner is not the customer.
This regualation rules generate then a conseguence on the DD Mandate subscription process. The firm can accept DD Mandate with debtor validated.
### Payment
The Payment context use the DD mandate offered by the Legal context to understand if it's possible accept the instruction received from the customer related a recurring payment of xx $$. 
Accepted the customer instruction (called Payment Order) then the context will generate the payment every time the instruction will ask to trigger a new payment.
All the business logic related to the payment order and Payment will stay here.
### Accounting
Accounting is the last context that we found in this use case and manage the assets of the customer. Every time a payment arrives to the firm and pass the payment context validation then it will be allocated on the customer account.
It's rapresent the assets of the customer in the financial firm.

We could have other contexts for example a CRM context to manage the  communications to the customer or maybe less context it's depends by your situation. 
If you want to know more about the contexts DDD and EventStorming are a good starting point



 using the [Domain Driven Design](https://martinfowler.com/tags/domain%20driven%20design.html)  approach and the [Hexagonal_architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)).

The service lives in the Legel Context, so this service doesn't manage any payment logic but implement only the business rules needed to manage the mandate as agreement between the Debitor and the Debitor.

To design the service I used the [EventStorming](https://www.eventstorming.com/) methodology. The outcome of the ES design is visible [here](#event-storming-design).

The Context map below should helps to contestualise the project and the parts not touched.


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
eyJoaXN0b3J5IjpbMTMyMzc1Mzc3MCwtMTcyOTY1MzMyNywtMj
AyNjc3NDM1MSwxODM4MzY0MjUsNTE4NjA2MTk2LC0xNDgwNzYw
NTUwXX0=
-->