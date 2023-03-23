import React from 'react'

const Description = () => (
  <div className='container-fluid'>
    <h2>Demo introduction</h2>
    <p>
  This demo show cases the different features provided by the privacy toolkit developed at IBM Research - Dublin.
    </p>
    <p>
  The toolkit consists of five main componets:
    </p>
    <ol>
      <li><b>Type identification</b></li>
      <li><b>Masking providers</b></li>
      <li><b>Privacy vulnerability identification</b></li>
      <li><b>Anonymization providers</b></li>
      <li><b>Privacy risk assessment</b></li>
      <li><b>Free text de-identification</b></li>
      <li><b>Image processing</b></li>
    </ol>
    <p>
  Each feature will be briefly described in the remainig of this text.
    </p>

    <h3>Type identification</h3>
    <p>
Our toolkit provides a type identification mechanism that frees the
user from the burden of specifying the types of the fields of the data
to be analyzed. This is a core feature of our toolkit for it was
designed to operate in a modern infrastructure where dynamic and
heterogenous schemas coexist. Our toolkit identifies more than 30
types; from general types like name, race, date and time to
industry-specific attributes like ICD codes (medical diseases) and
credit card numbers. The type identification mechanism is extensible;
it provides a programming interface for users to implement their own
identifiers based on either regular expressions or dictionaries.
    </p>

    <h3>Masking providers</h3>
    <p>
  Our toolkit provides masking functionality for all supported data
  types. The masking providers are designed and implemented with
  utility preservation in mind. For example, masking a credit card
  number in a utility-preserving way means to maintain the vendor
  information (VISA, Mastercard etc.) and randomize the unique
  identifier.
    </p>

    <p>Our masking framework supports two important properties:</p>
    <ul>
      <li>Consistent data masking: consistency is required for
    correlation across different masking operations.</li>
      <li>Compound data masking: our toolkit identifies relationships
    between the data and uses them as an input to the masking
    process.</li>
    </ul>
    <p>
  For example, a city attribute is linked with the country
  attribute. Whenever city attribute is masked, the country attribute
  reflects the correct country for the new value of the city. Compound
  data masking is essential to create more realistic masked
  output. Our toolkit detects and masks accordingly relationship for
  location data (city, country, continent), dates and numerical
  properties (sum, product, approximate sum).
    </p>
    <h3> Privacy vulnerability identification</h3>
    <p>
The privacy risk identification component identifies combinations in the data that appear less times than certain threshold. For example, a combination of age plus gender plus ZIP code in a dataset can appear 2 times, which practically means that an adversary can narrow down his/her re-identification attack to two people. Our toolkit includes a set of algorithms that perform privacy risk identification with scalability and performance in mind.
    </p>

    <h3>Anonymization providers</h3>
    <p>
Masking providers protect unique identifiers, like names, e-mails etc. Combinations of attributes that lead to privacy risks (also known as quasi-identifiers), such as the ones that are detected
by the privacy risk identification algorithms of our toolkit, require a separate anonymization process. Our toolkit includes anonymization algorithms that can be applied
to a dataset and protect the quasi-identifiers by replacing them with generalized values.
    </p>

  </div>
)

export default Description
