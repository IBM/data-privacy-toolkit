# Annotation of documents


## Syntax

The annotated segments of the document need to be prepended by `<ProviderType:NNN>` and appended by `</ProviderType>`. The `NNN` refers to the entity type that the annotated segments matches to. For the full list of entity types that are pre-defined see section 3

For example, in the text below we want to annotate that `John` is a NAME and `New York` is a CITY.

```
Hi, I am John and I live in New York
```

After the annotation, the document will look like:

```
Hi, I am <ProviderType:NAME>John</ProviderType> and I live in <ProviderType:CITY>New York</ProviderType>
```


## List of pre-defined entity types

| Identity name      | Description                   |
|--------------------|-------------------------------|
| ADDRESS            | Addresses                     |
| ATC                | ATC codes                     |
| BOOLEAN            | Boolean                       |
| CITY               | City                          |
| CONTINENT          | Continent                     |
| COUNTRY            | Country                       |
| COUNTY             | Counties                      |
| CREDIT\_CARD\_TYPE | Credit Card type              |
| CREDIT\_CARD       | Credit Card                   |
| DATETIME           | Date/Time                     |
| DAY                | Day masking provider type     |
| DEPENDENT          | Dependent provider type       |
| EMAIL              | E-mail                        |
| GENDER             | Genders                       |
| HOSPITAL           | Hospitals and medical centers |
| IBAN               | IBAN                          |
| ICDv10             | ICDv10                        |
| ICDv9              | ICDv9                         |
| IMEI               | IMEI                          |
| IMSI               | IMSI                          |
| IP_ADDRESS         | IP address                    |
| LATITUDE_LONGITUDE | Latitude/Longitude            |
| MAC_ADDRESS        | MAC Address                   |
| MARITAL_STATUS     | Marital Status                |
| MEDICINE           | Drug list                     |
| MONETARY           | Monetary value provider type  |
| MONTH              | Month masking provider type   |
| NAME               | Name                          |
| NATIONAL_ID        | National ID                   |
| NUMERIC            | Numeric                       |
| OCCUPATION         | Occupation                    |
| ORGANIZATION       | Organizations                 |
| PHONE              | Telephone numbers             |
| PROCEDURE          | Procedure                     |
| RACE               | Race/Ethnicity                |
| RELIGION           | Religion                      |
| SSN_UK             | Social Security Number UK     |
| SSN_US             | Social Security Number US     |
| STATES_US          | US States                     |
| STREET_TYPES       | Street types provider type    |
| SWIFT              | SWIFT code                    |
| SYMPTOM            | Symptom                       |
| URL                | URLs                          |
| VIN                | Vehicle Identification Number |
| YOB                | Year of birth                 |
| ZIPCODE            | ZIP codes                     |
