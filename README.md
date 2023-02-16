# IBM Data Privacy Toolkit (DPT)
[![DataPrivacyToolkit-Library](https://github.com/IBM/data-privacy-toolkit/actions/workflows/library.yml/badge.svg?branch=main)](https://github.com/IBM/data-privacy-toolkit/actions/workflows/library.yml)
[![DataPrivacyToolkit-Toolkit](https://github.com/IBM/data-privacy-toolkit/actions/workflows/toolkit.yml/badge.svg?branch=main)](https://github.com/IBM/data-privacy-toolkit/actions/workflows/toolkit.yml)
[![DataPrivacyToolkit-Spark](https://github.com/IBM/data-privacy-toolkit/actions/workflows/spark.yml/badge.svg?branch=main)](https://github.com/IBM/data-privacy-toolkit/actions/workflows/spark.yml)
![CodeQL](https://github.com/IBM/data-privacy-toolkit/actions/workflows/codeql.yml/badge.svg)
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
- [Documentation](docs/README.md)
- [Java Library](library)
- [Toolkit (CLI/Docker)](docker)
- [Spark wrappers](spark)

## Academic References:
- M. Kesarwani, A. Kaul, S. Braghin, N. Holohan, and S. Antonatos, "Secure k-anonymization over encrypted databases," 2021 IEEE 14th International Conference on Cloud Computing (CLOUD), 2021, pp. 20-30, doi: 10.1109/CLOUD53861.2021.00015.
- M. Pachilakis, S. Antonatos, K. Levacher, and S. Braghin, "PrivLeAD: Privacy Leakage Detection on the Web," Intelligent Systems and Applications. IntelliSys 2020. Advances in Intelligent Systems and Computing, vol 1250. Springer, Cham. doi: 10.1007/978-3-030-55180-3_32.
- S. Braghin, J. H. Bettencourt-Silva, K. Levacher, and S. Antonatos, "An Extensible De-Identification Framework for Privacy Protection of Unstructured Health Information: Creating Sustainable Privacy Infrastructures", In MEDINFO 2019: Health and Wellbeing e-Networks for All (pp. 1140-1144). IOS Press. doi: 10.3233/SHTI190404.
- S. Antonatos, S. Braghin, N. Holohan, Y. Gkoufas and P. Mac Aonghusa, "PRIMA: An End-to-End Framework for Privacy at Scale," 2018 IEEE 34th International Conference on Data Engineering (ICDE), 2018, pp. 1531-1542, doi: 10.1109/ICDE.2018.00171.
- N. Holohan, S. Antonatos, S. Braghin, and P. Mac Aonghusa, "(k, ϵ)-anonymity: k-anonymity with ϵ-differential privacy,", 2017, arXiv preprint arXiv:1710.01615.
- A. Gkoulalas-Divanis and S. Braghin, "IPV: A system for identifying privacy vulnerabilities in datasets," in IBM Journal of Research and Development, vol. 60, no. 4, pp. 14:1-14:10, July-Aug. 2016, doi: 10.1147/JRD.2016.2576818.
- A. Gkoulalas-Divanis, S. Braghin and S. Antonatos, "FPVI: A scalable method for discovering privacy vulnerabilities in microdata," 2016 IEEE International Smart Cities Conference (ISC2), 2016, pp. 1-8, doi: 10.1109/ISC2.2016.7580849.
- A. Gkoulalas-Divanis and S. Braghin, "Efficient algorithms for identifying privacy vulnerabilities," 2015 IEEE First International Smart Cities Conference (ISC2), 2015, pp. 1-8, doi: 10.1109/ISC2.2015.7366170.
