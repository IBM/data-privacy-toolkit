# Transaction Uniqueness Task

This task is in charge of computing the risk in terms of extracting additional information about from a transactional dataset.

Contrary to the vulnerability assessment task, the transaction uniqueness task allows a more focuse assessment of the privacy risk of the dataset.

In particular, it assumes that the user is able to provide two types of information:

1. A list of identifiers for the entity executing the transaction. Examples are credit card number, patient id, and so on and so forth. Note that this is not the unique identifier of the transaction, which uniquely identifies the movement and is assumed to not be meaningful from an attacked point of view.
2. A list of so called *externally observable* features. These features are the characteristics about the current transaction that can be expeted to be accessed by the attacked. Examples are transaction date, amounts of credit card charges, discharge dates, etc.

The `taskConfiguration` field for this task accepts the following structure:

```json
{
	"targetColumns":  ["date", "location"],
	"idColumns": ["id"],
	"factor":  1,
	"threshold": 1
}
```

Where `targetColumns` is the list of *externally observable* fields, `idColumns` is the list of identifiers for the entity involving the transaction and `threshold` is the number of unique transactions required to not be considered unique. In the example, only unique (distinct) transactions are considered unique, but in other examples this could be set to 2 (or more), meaning that even cluster of 2 (or more) transactions with the same value in the target columns are still considered unique.

## Output

The output of this task is a report indicating statistics about the dataset as follows:

```json
{
	"totalIDs": 123,
	"totalTransactions": 100000,
	"uniqueTransactions": 5,
	"uniqueIDs": 2
}
```

This example shows a dataset containing 123 unique identifiers executing 100000 transactions. Of these, 5 transactions are uniquely identifiable and they refer to 2 identifiers.

## Interpretation of the results

These results should be used to decide which type of de-identification strategy should be used to reduce teh risk for an attacker to gain knowledge based on the externally observable fields.
Thise could result in reducing precision in some of the externally observable fields, such as binning transaction amount to 5 USD intervals or reducing the precision of the timestamps, or via suppression of unique transactions if the number is small enough