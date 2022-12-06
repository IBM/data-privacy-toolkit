# IBM Data Privacy Toolkit (DPT)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](./CODE_OF_CONDUCT.md) 

The IBM Data Privacy Toolkit, (formerly known as Privacy Masking and Anonymization, or PRIMA) is a toolkit for data type identification, privacy risk assessment, data masking and data anonymization that is exposed as a Java/Scala library and as a REST API.
The toolkit consists of four main components:
- **Type identification**
- **Masking providers**
- **Privacy risk assessment**
- **Anonymization providers**

Each feature will be briefly described in the remaining of this text.

## Type identification
Our toolkit provides a type identification mechanism that frees the user from the burden of specifying the types of the fields of the data to be analysed.
This is a core feature of our toolkit for it was designed to operate in a modern infrastructure where dynamic and heterogenous schema coexist.
Our toolkit identifies more than 30 types; from general types like name, race, date and time to industry-specific attributes like ICD codes (medical diseases) and credit card numbers.
The type identification mechanism is extensible; it provides a programming interface for users to implement their own identifiers based on either regular expressions or dictionaries.

## Masking providers
Our toolkit provides masking functionality for all supported data types.
The masking providers are designed and implemented with utility preservation in mind.
For example, masking a credit card number in a utility-preserving way means to maintain the vendor information (VISA, MasterCard etc.) and randomise the unique identifier.

Our masking framework supports two important properties:

- **Consistent data masking**: consistency is required for correlation across different masking operations.
- **Compound data masking**: our toolkit identifies relationships between the data and uses them as an input to the masking process.
For example, a city attribute is linked with the country attribute.
Whenever city attribute is masked, the country attribute reflects the correct country for the new value of the city.
Compound data masking is essential to create more realistic masked output.
Our toolkit detects and masks accordingly relationship for location data (city, country, continent), dates and numerical properties (sum, product, approximate sum).

## Privacy risk identification
The privacy risk identification component identifies combinations in the data that appear less times than certain threshold.
For example, a combination of age plus gender plus ZIP code in a dataset can appear 2 times, which practically means that an adversary can narrow down his/her re-identification attack to two people.
Our toolkit includes a set of algorithms that perform privacy risk identification with scalability and performance in mind.

## Anonymization providers
Masking providers protect unique identifiers, like names, e-mails etc.
Combinations of attributes that lead to privacy risks (also known as quasi-identifiers), such as the ones that are detected by the privacy risk identification algorithms of our toolkit, require a separate anonymization process.
Our toolkit includes anonymization algorithms that can be applied to a dataset and protect the quasi-identifiers by replacing them with generalised values.



## References
- Documentation ([Online](https://pages.github.ibm.com/Dublin-Research-Lab/ibm-data-privacy-toolkit-docs/)) [repo](https://github.ibm.com/Dublin-Research-Lab/ibm-data-privacy-toolkit-docs)
- PRIMA is an Alpha level service in [IBM.Next](https://ibmnext.stage1.mybluemix.net/assets/primaservice)
- Toolkit (CLI) [repo](https://github.ibm.com/Dublin-Research-Lab/ibm-data-privacy-toolkit-cli)
- Demo (web-based UI for demo purposes only) [repo](https://github.ibm.com/Dublin-Research-Lab/prima-demo)
- Spark wrappers [repo](https://github.ibm.com/Dublin-Research-Lab/ibm-data-privacy-toolkit-spark)
- S. Antonatos, S. Braghin, N. Holohan, Y. Gkoufas and P. Mac Aonghusa, "PRIMA: An End-to-End Framework for Privacy at Scale," 2018 IEEE 34th International Conference on Data Engineering (ICDE), 2018, pp. 1531-1542, doi: 10.1109/ICDE.2018.00171.
- A. Gkoulalas-Divanis and S. Braghin, "IPV: A system for identifying privacy vulnerabilities in datasets," in IBM Journal of Research and Development, vol. 60, no. 4, pp. 14:1-14:10, July-Aug. 2016, doi: 10.1147/JRD.2016.2576818.
- A. Gkoulalas-Divanis, S. Braghin and S. Antonatos, "FPVI: A scalable method for discovering privacy vulnerabilities in microdata," 2016 IEEE International Smart Cities Conference (ISC2), 2016, pp. 1-8, doi: 10.1109/ISC2.2016.7580849.
- A. Gkoulalas-Divanis and S. Braghin, "Efficient algorithms for identifying privacy vulnerabilities," 2015 IEEE First International Smart Cities Conference (ISC2), 2015, pp. 1-8, doi: 10.1109/ISC2.2015.7366170.
