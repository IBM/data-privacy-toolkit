# Table of Contents
1. [Masking a single value](#single)
2. [Masking named fields](#named)
3. [Loading your own masking configuration](#ownconf)
4. [Masking configuration file structure](#maskconf)
5. [List of masking provider types with their options and system default values](#masklist)
6. [Performing consistent masking](#persistent)

## Masking a single value <a name="single"></a>

The following example will mask a single value using the default masking configuration.

```java
import com.ibm.research.drl.prima.configuration.ConfigurationManager;
import com.ibm.research.drl.prima.configuration.MaskingConfiguration;
import com.ibm.research.drl.prima.providers.ProviderType;
import com.ibm.research.drl.prima.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.prima.providers.masking.MaskingProvider;
import com.ibm.research.drl.prima.providers.masking.AbstractMaskingProvider;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public String maskName(String originalName) {
	//defaults will be loaded
    ConfigurationManager configurationManager = new ConfigurationManager(); 
    MaskingProviderFactory maskingProviderFactory = new
    		MaskingProviderFactory(configurationManager, null);

    /* Get a MaskingProvider from the factory */
    MaskingProvider maskingProvider = maskingProviderFactory.get(
    		ProviderType.NAME, 
    		configurationManager.getDefaultConfiguration());

    /* Apply the masking provider to the original value. mask() 
       only accepts String values */
    String maskedValue = maskingProvider.mask(originalName);
    return maskedValue;
}
```

## Masking named fields <a name="named"></a>

```java
public void maskNamedFields() {       

    //defaults will be loaded. See below for loading your own configuration
    ConfigurationManager configurationManager = new ConfigurationManager(); 

    Map<String, ProviderType> identifiedTypes = new HashMap<>();
    identifiedTypes.put("name", ProviderType.NAME);

    /* Instantiate a MaskingProviderFactory with a 
    ConfigurationManager and the map of identified types */
    MaskingProviderFactory maskingProviderFactory = new
    		MaskingProviderFactory(configurationManager, identifiedTypes);

    /* Get the masking provider and mask a value */
    MaskingProvider maskingProvider = maskingProviderFactory.get("name");
    String maskedValue = maskingProvider.mask("John Smith");
    System.out.println(maskedValue);
}
```

## Loading your own masking configuration <a name="ownconf"></a>

If you do not want to use system defaults, you can initialize the configuration manager to load a configuration file.

```java
ConfigurationManager configurationManager = ConfigurationManager.load("/tmp/maskingConfiguration.json");
```
 
The following overloads are available:

```java
public static ConfigurationManager load(final InputStream inputStream) throws IOException {}`
```

## Masking configuration file structure <a name="maskconf"></a>

Masking configuration can be loaded from a JSON file or InputStream. The JSON contents should look like this:

```json
{
   "_defaults": {
   },
   "_fields": {
   }
}
```

Masking configuration options control how the masking providers behave. For example, 
we want to mask the e-mail field named `emailField` using the EMAIL masking provider. By default, the EMAIL masking provider preserves one domain level, since the default value of `email.preserve.domains` is set to 1 (see the relevant section in the list of options). So, the e-mail `username@foo.edu.ie` will be masked to `randomusernamehere@randomdomain.randomdomain2.ie` (note that `.ie` was preserved). Let's assume that we want to preserve two domain levels (that is preserve `.edu.ie`). We have two options to alter the behavior of the masking provider:

1) We override the configuration option in the `_defaults` section of the masking configuration. The `_defaults` section will now look like:

```json
{
	"_fields": {
	},
	"_defaults": {
		"email.preserve.domains": 2
	}
}
```
All the EMAIL masking providers will now be initialized to preserve two domain levels. The `_defaults` section overrides the values of the system defaults encoded in the library.

2) We override the per-field configuration in the `_fields` section. The section will now look like:

```json
{
	"_fields": {
		"emailField": {
			"email.preserve.domains": 2
		}
	},
	"_defaults": {
	}
}
```
The per-field configuration allows us to alter the behavior of that masking provider only for that field. If, for example, there is another email field in the file, lets name it recipientEmail, and we want to apply the EMAIL masking provider to it, then its masking provider will be initialized using the `_defaults` section and/or the system defaults (given the configuration above).

The configuration options in the `_fields` section have priority over the `_defaults` which in their turn have priority over the system defaults.


### Configuration for consistent masking

Let's assume that we have an input file with two columns, f0 and f1. By default, non-consistent masking is applied. We want to consistently mask the f0 column using the `BINNING` masking provider. To achieve this, we need to set `persistence.export` to true in the per-field configuration for f0. The default setting is to do consistent masking in-memory, thus the masking results will be consistent for that session but the results of the consistent replacement will be lost at exit. 

The consistent masking requires a namespace to be provided every time we enable `persistence.export` to true. The namespace value is user-provided

```json
{
  "_fields" : {
   "f0": {
  		"persistence.export": true,
  		"persistence.type": "memory",
  		"persistence.namespace": "f0_ns"
  	}
  },
  "_defaults": {
  }
}
```

If we want to have consistent masking across exports, we need to enable it by setting the correct configuration options to the per-field configuration. We need to set `persistence.type` to `"file"` and `persistence.file` should include a directory where we have permission to store the mappings.
The following example, will store the consistent masking results to `/tmp/f0_ns`.
The next time the toolkit runs, it will initialize the masking mapping for that column from that file, thus ensuring consistent results across different executions.


```json
{
  "_fields" : {
   "f0": {
  		"persistence.export": true,
  		"persistence.type": "file",
  		"persistence.file": "/tmp/",
  		"persistence.namespace": "f0_ns"
  	}
  },
  "_defaults": {
  }
}
```


## List of masking provider types with their options and system default values <a name="masklist"></a>

There are some options that apply to many masking providers.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|fail.mode|Integer|Controls how to handle cases where the value cannot be parsed or masked. Possible values are 1, 2, 3 and 4. Value 1 means that the original value will be returned. Value 2 means an empty value will be returned. Value 2 throws an error. Value 4 returns a randomly generated value. |2|

#### ADDRESS

Masks an address with a random one. Various elements of the address can be preserved like street names and road types.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|address.postalCode.nearest|Boolean|Select nearest postal code|false|
|address.roadType.mask|Boolean|Mask road type (street, avenue, etc)|true|
|address.postalCode.nearestK|Integer|Number of closest postal codes to select from|10|
|address.country.mask|Boolean|Mask country|true|
|address.postalCode.mask|Boolean|Mask postal code|true|
|address.number.mask|Boolean|Mask number|true|
|address.city.mask|Boolean|Mask city|true|
|address.mask.pseudorandom|Boolean|Mask based on pseudorandom function|false|
|address.streetName.mask|Boolean|Mask street name|true|

#### PHONE

Replaces a phone number with a random one. There are options to preserve the country code and area code.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|phone.countryCode.preserve|Boolean|Preserve country code|true|
|phone.areaCode.preserve|Boolean|Preserve area code|true|

#### VIN

Masks vehicle identifier number with the options to preserve the manufacturer and vehicle description information.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|vin.wmi.preserve|Boolean|Preserve manufacturer information (WMI)|true|
|vin.vds.preserve|Boolean|Preserve vehicle description information (VDS)|false|

#### SSN_US

Masks Social Security Numbers based on the US with the option to preserve their prefix

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|ssnus.mask.preserveAreaNumber|Boolean|Preserve area number|true|
|ssnus.mask.preserveGroup|Boolean|Preserve group|true|

#### HASH

Hashes the original value given an algorithm name. See <https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest> for a list of available providers.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|hashing.algorithm.default|String|Default algorithm|SHA-256|
|hashing.salt|String|Salt to be used during hashing|""|

#### COUNTY

Masks a county by replacing it with a random one.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|county.mask.pseudorandom|Boolean|Mask based on pseudorandom function|false|

#### URL

Masks URLs with the options to remove the query part, preserve domain levels, mask ports and username/passwords present

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|url.mask.port|Boolean|Mask port|false|
|url.mask.removeQuery|Boolean|Remove query part|false|
|url.preserve.domains|Integer|Number of domains to preserve|1|
|url.mask.usernamePassword|Boolean|Mask username and password|true|
|url.mask.maskQuery|Boolean|Mask query part|false|

#### NAME

Masks names. There are three options to mask names and there are checked in that order: a) generate a pseudorandom name (`names.mask.pseudorandom` controls this behavior) b) use custom masking functionality through virtual fields (`names.mask.virtualField` controls this) c) return a random name from the loaded dictionaries when the previous options are both disabled.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|names.masking.allowUnisex|Boolean|Allow unisex names to be used for masking|false|
|names.masking.separator|String|The separator to use when splitting a name into its individual tokens|
|names.mask.pseudorandom|Boolean|Provide pseudodandom consistence|false|
|names.mask.virtualField|String|Treat name tokens as a virtual field and mask them according to the masking configuration of that virtual field|null|

#### RACE

Replaces races with a random one.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|race.mask.probabilityBased|Boolean|Masks the value based on probabilities defined in the resource file|false|

#### GENDER

Replaces a gender with a random one. This provider does not have any configuration options

#### RELIGION

Replaces a religion with a random one.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|religion.mask.probabilityBased|Boolean|Masks the value based on probabilities defined in the resource file|false|

#### BINNING

Bins numerical values based on a bin size and a configurable format. Default is bin size of 5 and the format is %s-%s, for example 2002 will be replaced by 2000-2005

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|binning.mask.format|String|The format of the binned value|%s-%s|
|null.mask.returnNull|Boolean|The format of the binned value|false|
|binning.mask.binSize|Integer|The bin size|5|
|binning.mask.returnBinMean|Boolean|Return the mean of the bin|false|

#### SHIFT

Shifts a numerical value by a fixed offset.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|shift.mask.value|Double|The offset for shifting|1.0|

#### IP_ADDRESS

Masks IP addresses with the option to preserve subnets.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|ipaddress.subnets.preserve|Integer|Number of prefixes to preserve|0|

#### EMAIL

Masks e-mail addresses with the option to preserve certains level of the host domain and
create a username based on random combination of names/surnames.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|email.preserve.domains|Integer|Number of domains to preserve|1|
|email.nameBasedUsername|Boolean|Name-based usernames|false|

#### IMSI

Masks IMSI identifiers with the option to preserve MCC and MNC

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|imsi.mask.preserveMNC|Boolean|Preserve MNC|true|
|imsi.mask.preserveMCC|Boolean|Preserve MCC|true|

#### Export
|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|export.relationships.perRecord|Boolean|Per record relationship extraction|false|
|export.sampling|Integer|Sampling percentage|100|

#### OrderPreservingEncryption

Masks a date using order preserving encryption. This provider is extremely slow for large scale masking

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|ope.out.start|Integer|Default OUT-range start|0|
|ope.default.key|String|Default encryption key|7579687b-33f5-4f5d-9127-3d7d21d9e028|
|ope.out.end|Integer|Default OUT-range end|2147483646|
|ope.in.start|Integer|Default IN-range start|0|
|ope.in.end|Integer|Default IN-range end|32767|

#### SSN_UK

Masks SSN based on the UK with the option to preserve their prefix

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|ssnuk.mask.preservePrefix|Boolean|Preserve prefix|true|


#### REPLACE

Replaces a value with either asterisks or with random characters. Digits are replaced by digits and alpha characters with random alpha characters. Other characters like dashes, commas, etc. are preserved.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|replace.mask.replaceWithAsterisks|Boolean|Replace the rest of the value with asterisks|false|
|replace.mask.preserve|Integer|Number of characters to preserve|3|
|replace.mask.offset|Integer|Starting offset for preserving|0|
|replace.mask.replaceWithRandom|Boolean|Replace the rest of the value with random digits/characters|false|

#### CREDIT_CARD

Masks credit card number with the option to preserve the issuer (VISA, Mastercard, etc.)

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|creditCard.issuer.preserve|Boolean|Preserve issuer|true|

#### IBAN

Masks IBAN account numbers with the option to preserve country

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|iban.mask.preserveCountry|Boolean|Preserve country code|true|

#### MONETARY

Replaces monetary values

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|monetary.replacing.character|String|Replacement character for digits|X|
|monetary.preserve.size|Boolean|Preserve the number of digits|true|

#### DATETIME

Masks datetime objects. There are several options like shifting dates, generalizing to month, year etc. or adding random offsets to the various datetime elements (years, months, days, hours, seconds etc.). If multiple options are set to true the following priority list is respected (examples are for 10th of January 2016):

1. Shift date by constant amount. Option `datetime.mask.shiftDate` controls this behavior
2. Generalize to week number/year (like 02/2016). See option `datetime.generalize.weekyear`
3. Generalize to month/year (like 01/2016). See option `datetime.generalize.monthyear`
4. Generalize to quarter year (like 01/2016). See option `datetime.generalize.quarteryear`
5. Generalize to year (2016). See option `datetime.generalize.year`
6. Generalize to N-year interval (like 2015-2020). See option `datetime.generalize.nyearinterval`
7. Replace day with another day of the same class (weekday, weekend). See option `datetime.mask.replaceDaySameClass`
8. Apply differential privacy to the dates. See option `datetime.mask.replaceDayWithDiffPriv`
9. Add random offsets to year, month, day, hour, minutes, seconds as specified. See options `datetime.year.mask`, `datetime.month.mask`, `datetime.day.mask`, `datetime.hour.mask`,  `datetime.minutes.mask`, `datetime.seconds.mask`

For specifying fixed date formats, the notation as specified in [https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html) must be used. Important notice: for specifying years the year-of-era symbol `y` must be used. Specifying year in the week-based-year symbol `Y` will result into an error.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|datetime.mask.shiftDate|Boolean|Shift date by a constant amount|false|
|datetime.mask.shiftSeconds|Integer|Seconds to shift date by|0|
|datetime.generalize.year|Boolean|Generalize to year|false|
|datetime.generalize.nyearinterval|Boolean|Generalize to N-year interval|false|
|datetime.generalize.weekyear|Boolean|Generalize to week/year|false|
|datetime.generalize.monthyear|Boolean|Generalize to mm/year|false|
|datetime.generalize.quarteryear|Boolean|Generalize to quarter/year|false|
|datetime.generalize.nyearintervalvalue|Integer|Value of for N-year interval generalization|0|
|datetime.mask.replaceDaySameClass|Boolean|Replace a weekday with another random weekday and a weekend day with another random weekend day|false|
|datetime.mask.replaceDayWithDiffPriv|Boolean|Replace date using differential privacy|false|
|datetime.mask.replaceDayWithDiffPrivEpsilon|Double|Epsilon to be used when applying differential privacy|3.0|
|datetime.year.mask|Boolean|Mask year|true|
|datetime.year.rangeUp|Integer|Mask year range upwards|0|
|datetime.year.rangeDown|Integer|Mask year range downwards|10|
|datetime.month.mask|Boolean|Mask month|true|
|datetime.month.rangeUp|Integer|Mask month range upwards|0|
|datetime.month.rangeDown|Integer|Mask month range downwards|12|
|datetime.day.mask|Boolean|Mask day|true|
|datetime.day.rangeUp|Integer|Mask day range upwards|0|
|datetime.day.rangeDown|Integer|Mask day range downwards|7|
|datetime.hour.mask|Boolean|Mask hour|true|
|datetime.hour.rangeUp|Integer|Mask hour range upwards|0|
|datetime.hour.rangeDown|Integer|Mask hour range downwards|100|
|datetime.minutes.mask|Boolean|Mask minutes|true|
|datetime.minutes.rangeUp|Integer|Mask minutes range upwards|0|
|datetime.minutes.rangeDown|Integer|Mask minutes range downwards|100|
|datetime.seconds.mask|Boolean|Mask seconds|true|
|datetime.seconds.rangeUp|Integer|Mask seconds range upwards|0|
|datetime.seconds.rangeDown|Integer|Mask seconds range downwards|100|
|datetime.format.fixed|String|Datetime format|null|
|datetime.mask.keyBasedMaxDays|Integer|Maximum number of days when using key-based compound masking|100|
|datetime.mask.keyBasedMinDays|Integer|Minimum number of days when using key-based compound masking|0|

#### ZIPCODE

Masks a ZIP code. The system includes the ZIP code information for US. The user can specify if minimum population is required to be checked and if true the minimum population threshold.
The minimum population is checked against the information stored from the latest US census (2010) and it is checked at a prefix-based level based on the number of digits specified by `zipcode.mask.minimumPopulationPrefixDigits`. By default, minimum population is set to 20000 and it is checked based on 3-digit prefixes.


|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|zipcode.mask.requireMinimumPopulation|Boolean|Require minimum population|true|
|zipcode.mask.minimumPopulationPrefixDigits|Integer|Prefix for minimum population|3|
|zipcode.mask.countryCode|String|Country for the zip codes (2-digit ISO code)|US|
|zipcode.mask.minimumPopulation|Integer|Minimum Population|20000|
|zipcode.mask.minimumPopulationUsePrefix|Boolean|Use prefix for minimum population|true|


#### HASHINT

Returns an integer value by hashing the original value.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|hashint.budget.amount|Integer|Amount of randomization to be added|10|
|hashint.budget.use|Boolean|Limit the randomization within a budget|false|
|hashint.algorithm.default|String|Default algorithm|SHA-256|
|hashint.sign.coherent|Boolean|The masked value has to be coherente with respect to the sign|true|

#### REDACT

Redacts a value by replacing it with a character (default is asterisk). The length of the original value can be optionally preserved

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|redact.preserve.length|Boolean|Preserve token length|true|
|redact.replace.character|String|Default replacement character|X|

#### NULL

Replaces the original value with an empty string

#### IMEI

Masks IMEI attributes with the option to preserve the TAC information

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|imei.mask.preserveTAC|Boolean|Preserve TAC prefix|true|

#### CITY

Masks a city with a random one or based on one of its neighbors (geographical distance).

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|city.mask.closest|Boolean|Select one of the near cities|false|
|city.mask.closestK|Integer|Number of closest cities to select from|10|
|city.mask.pseudorandom|Boolean|Mask based on pseudorandom function|false|

#### ICDv9

Masks ICDv9 codes. Codes can also be generalized to their chapters or categories.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|icd.randomize.chapter|Boolean|Randomize by chapter|false|
|icd.randomize.category|Boolean|Randomize by 3-digit code|true|

#### ICDv10

Masks ICDv10 codes. Codes can also be generalized to their chapters or categories.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|icd.randomize.chapter|Boolean|Randomize by chapter|false|
|icd.randomize.category|Boolean|Randomize by 3-digit code|true|

#### MAC_ADDRESS

Masks MAC addresses with the option to preserve the vendor information

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|mac.masking.preserveVendor|Boolean|Preserve vendor information|true|

#### Persistence

These options control the persistence when masking value. If `persistence.export` is set to true then an original value will always be masked to the same value. `persistence.type` defines the medium to use for storing the mappings. File-based, memory-based and database backends are supported.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|persistence.export|Boolean|Persistence per export|false|
|persistence.type|String|The backend system to use for storing the mappings. Possible values are `file`, `memory` and `database`|memory|
|persistence.namespace|String|Persistence global namespace|null|
|persistence.schema|Boolean|Persistence per schema|false|
|persistence.file|String|Directory for the file-based backend|
|persistence.database.username|String|Username for database connection|""|
|persistence.database.password|String|Password for database connection|""|
|persistence.database.driverName|String|Driver name for database connection|""|
|persistence.database.connection|String|Connection string for database connection|""|
|persistence.database.cacheLimit|Integer|Cache limit in bytes for database-backed persistence|""|

#### ATC

Masks an ATC code with the option to preserve certain levels.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|atc.mask.levelsToKeep|Integer|Number of levels to keep|4|

#### OCCUPATION

Replaces an occupation with a random one or generalizes it to its category. Categories are based on the 2010 SOC classification.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|occupation.mask.generalize|Boolean|Generalize to occupation category|false|

#### CONTINENT

Masks a continent by replacing it with a random one or by a closest one (distance based on an approximate centroid of the continent's bounding box)

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|continent.mask.closest|Boolean|Select one of the nearest continents|false|
|continent.mask.closestK|Integer|Number of neighbors for nearest continents|5|

#### HOSPITAL

Replaces a hospital name with another one

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|hospital.mask.preserveCountry|Boolean|Select a hospital from the same country|true|

#### Defaults

The `default.masking.provider` option controls what masking provider to invoke in case none is specified or a provider is not found

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|default.masking.provider|String|Default masking provider|RANDOM|

#### NUMBERVARIANCE

Masks a numeric value by adding a +/- offset based on given percentages. Limits cannot be negative numbers. If precision digits is -1, the final result will be returned with its original precision, else the digit part will be trimmed accordingly.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|numvariance.mask.limitUp|Double|Up percentage limit|10.0|
|numvariance.mask.limitDown|Double|Down percentage limit|10.0|
|numvariance.mask.precisionDigits|Integer|Number of precision digits|-1|

#### COUNTRY

Replaces a country with another random one or by a nearest country

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|country.mask.closestK|Integer|Number of nearest countries to select from|10|
|country.mask.closest|Boolean|Select one of the near countries|false|
|country.mask.pseudorandom|Boolean|Mask based on pseudorandom function|false|

#### LATITUDE_LONGITUDE

Masks latitude/longitude pairs. Recognizes several formats

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|latlon.mask.fixedRadiusRandomDirection|Boolean|Random a point with a fixed radium but random direction|false|
|latlon.mask.donutMasking|Boolean|Randomize a point in a donut-shaped|false|
|latlon.mask.randomWithinCircle|Boolean|Randomize a point in a circle|true|
|latlon.offset.maximumRadius|Integer|Maximum offset radius (in meters)|100|
|latlon.offset.minimumRadius|Integer|Minimum Offset radius (in meters)|50|

#### SWIFT

Masks SWIFT codes using in the banking domain

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|swift.mask.preserveCountry|Boolean|Preserve Country|false|

#### DICTIONARY_BASED

Masks based on a random value selected from a given dictionary. The contents of the file should be one dictionary term per line

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|dictionaryBased.mask.filename|String|Filename of the dictionary to load|""|

#### HADOOP_DICTIONARY_BASED

Masks based on a random value selected from a given dictionary. The contents of the file should be one dictionary term per line. The file is retrieved from HDFS. Warning: This masking provider is available only when running the Spark version of the toolkit.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|hadoop.dictionary.path|String|Filename of the dictionary to load|""|

#### RATIO_BASED

Multiplies the value by the given ration and returns the result.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|ratiobased.mask.ratio|Double|Ratio to use|1.0|
|ratiobased.mask.precisionDigits|Integer|Precision for the decimal part|-1|

#### DIFFERENTIAL_PRIVACY

Applies differential privacy to a given attribute

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|differentialPrivacy.mechanism|String|Mechanism to use. Options are LAPLACE\_NATIVE, LAPLACE\_BOUNDED, LAPLACE\_TRUNCATED, CATEGORICAL and BINARY|LAPLACE_NATIVE|
|differentialPrivacy.parameter.epsilon|Double|Epsilon to use|8.0|

In case that LAPLACE\_NATIVE, LAPLACE\_BOUNDED or LAPLACE\_TRUNCATED was selected as a mechanism, then the following options are required:

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|differentialPrivacy.range.min|Double|Minimum value of the domain|0|
|differentialPrivacy.range.max|Double|Maximum value of the domain|0|

In case that the BINARY mechanism is applied, the following options are required:

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|differentialPrivacy.binary.value1|String|The first binary value|TRUE|
|differentialPrivacy.binary.value2|String|The second binary value|FALSE|

Finally, for the case of the CATEGORICAL mechanism:

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|differentialPrivacy.categorical.hierarchyName|String|The name of the hierarchy to be used|""|
|differentialPrivacy.categorical.hierarchyMap|JsonNode|A JSON object with the hierarchies specifications. See Section "Specifying hierarchies" below for more details/|null|

#### GENERALIZATION

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|generalization.masking.hierarchyName|String|The name of the hierarchy to be used|""|
|generalization.masking.hierarchyMap|JsonNode|A JSON object with the hierarchies specifications. See Section "Specifying hierarchies" below for more details|null|
|generalization.masking.hierarchyLevel|Integer|The generalization level to apply|-1|
|generalization.masking.randomizeOnFail|Boolean|If the value is not found in the hierarchy, then return a random result|true|

#### FREE_TEXT

The default behavior of the natural language text de-identification provider is to mask the entities. It also supports the options to return only annotated text, redact or tag the entities (by default all three options are disabled). Please see separate documentation for free text configuration specification.

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|freetext.mask.complexConfigurationFilename|String|The path to the configuration file for initializing the NLP pipeline. See separate documentation on how to write this configuration file|/complexWithIdentifiers.json|
|freetext.mask.complexConfigurationResourceType|String|The type of the configuration resource. This can be either `INTERNAL_RESOURCE` when reading from a file loaded in the classpath or `EXTERNAL_FILENAME` when reading from a file in the filesystem|`INTERNAL_RESOURCE`|
|freetext.mask.maskingConfigurationFilename|String|The path to the configuration file for masking|/c.json|
|freetext.mask.maskingConfigurationResourceType|String|The type of the masking configuration resource. This can be either `INTERNAL_RESOURCE` when reading from a file loaded in the classpath or `EXTERNAL_FILENAME` when reading from a file in the filesystem|`INTERNAL_RESOURCE`|

The free text masking provider supports a relationship type `GREP_AND_MASK` that gets tokens from a field, looks them up in the free text field and then masks them.
As an example, let's assume we want to mask the OBX element of an HL7 message (OBX contains comments in free text). We want to get the patient names from elements
`/.PID-5-1` and then locate them and mask them in the `/.OBX-5-1` element. The following options will be used for this compound masking:

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|generic.lookupTokensSeparator|String|Separator string for splitting into tokens|"^"|
|generic.lookupTokensIgnoreCase|Boolean|Ignore case when searching for tokens|false|
|generic.lookupTokensFindAnywhere|Boolean|If set to true tokens are search everywhere in the text, else they are searched as words|false|
|generic.lookupTokensType|String|Type to use for the matching tokens|""|


#### TAG

This provider does not take any options. It returns the field name appended with a unique id based on the value.

#### Excel masking provider

|Option name|Type|Description|Default value|
|-----------|----|-----------|-------------|
|excel.mask.ignoreNonExistent|Boolean|Control the behavior when a cell does not exist|true|

##### Specifying hierarchies

We have three ways to specify a hierarchy:

* Materialized hierarchy. In that case, the value is a JSON array of arrays. Each individual array consists of string elements. An example of a materialized hierarchy is as follows:

```json
[
    ["M", "Person"],
    ["F", "Person"]    
]
```

In the configuration example above, we specify a gender hierarchy as a materialized hierarchy: we have two possible values, `M` and `F` that are generalized to the value `Person`.

* Predefined hierarchy. The toolkit comes with a set of predefined materialized hierarchies. In this case, the value is a string that defines the name of the predefined hierarchy.


The following table contains a list of the available hierarchies along with the provider type that needs to be used as a parameter and the description of the generalization levels:

|Hierarchy|Key name|Generalization levels|
|---|---|---|
|City|CITY|City, country, continent, \*|
|Country|COUNTRY|Country, continent, \*|
|Gender|GENDER|Gender, \*|
|Race|RACE|Race, \*|
|Marital status|MARITAL\_STATUS|Marital status, category (alone or in-marriage), \*|
|ZIP code|ZIPCODE|5-digit value, 4-digit value, 3-digit value, 2-digit value, 1-digit value, \*|

* Custom loaded hierarchy. The user is able to implement their own hierarchies and load them in the toolkit. In this case, the value of the key is an object that contains:
    1.	The `className` which defines the class name to be loaded. It must be a fully qualified class name
    2. An `options` field that contains the options to be passed to the initialization of the hierarchy. The options are passed as `JsonNode` instances to the constructor of the hierarchy

As an example, the specification :

```json
{
    "className": "com.ibm.research.hierarchies.custom.age",
   	"options": {}
}
```

is a custom loaded hierarchy.
The class `com.ibm.research.hierarchies.custom.Age` is loaded and empty options are passed to its constructor.

## Masking various input formats

The basic principle is to create a `FormatProcessor` for the given format and then call

```java
void maskStream(InputStream input, PrintStream output,
                    MaskingProviderFactory factory,
                    DataMaskingOptions dataMaskingOptions, Set<String> alreadyMaskedFields,
                    Map<ProviderType, Class<? extends MaskingProvider>> registerTypes) throws IOException;
```

* `input` is an InputStream to the data
* `output` is the output stream to write the masked data

A `FormatProcessor` object can be instantiated from the FormatProcessorFactory's `getProcessor(DataTypeFormat datatypeFormat)`

### CSV

An example where we HASH the first column of a CSV file (assuming no header)

```java
public void testCSVMasking(InputStream is, PrintStream out, boolean hasHeader) throws Exception {
    DatasetOptions datasetOptions = new CSVDatasetOptions(hasHeader, ',', '"', false);
   
    FormatProcessor csvProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.CSV);
	
	ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

    String path = "Column 0";

    //we create a map of what we want to mask and how. See path notation on next section
    Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
    identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));

    //we create the masking options that contains all the information required for masking
    DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    identifiedTypes, false, null, datasetOptions);
                    
    MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
    
    //we process the input, the masked contents will be printed in the output
    csvProcessor.maskStream(inputStream, output, factory, dataMaskingOptions,  Collections.emptySet(), 				Collections.emptyMap()); 
    
    //print/check output here
}
```

### JSON, DICOM, XLS, XLSX, XML, PARQUET, VCF

Similar to CSV case but changing the DataTypeFormat to the factory call as well as the dataset options should be set to `null`. The `InputStream` should be a regular input stream to the contents of the data
(like a FileInputStream).


### JDBC

```java

private static Connection getConnection() throws ClassNotFoundException, SQLException
{
        String DBUrl      = "jdbc:postgresql://localhost/test";
        String DBUsername = "postgres";
        String DBPassword = ""; // Need password

        //Class.forName(DBDriver);
        return DriverManager.getConnection(DBUrl, DBUsername, DBPassword);
 }

public void testDatabase() {
	String schemaName = "s1";
	String tableName = "people";
	
	ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());
   MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, Collections.emptyMap());
    
   String path = "name";

   Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
   identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));
            
	InputStream jdbcStream  = new ResultSetInputStream(getConnection(), schemaName, tableName);
	
	FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.JDBC);
	
	PrintStream output = new JDBCOutputStream(getConnection(), "public", targetTable,
                true, "public", "test", true);
   
   formatProcessor.maskStream(jdbcStream, output, factory, dataMaskingOptions, Collections.emptySet(),  Collections.emptyMap());
	
}
```

## Path notations for various types

### CSV

!INCLUDE "../common/csv-path-notation.md"

### JSON

!INCLUDE "../common/json-path-notation.md"

### XML

!INCLUDE "../common/xml-path-notation.md"

### DICOM

!INCLUDE "../common/dicom-path-notation.md"

### XLS/XLSX 

!INCLUDE "../common/xls-path-notation.md"

### HL7 

!INCLUDE "../common/hl7-path-notation.md"

### VCF 

Similar as CSV

## Performing consistent masking <a name="persistent"></a>

Consistent masking ensures that all instances of a given original value will be masked to the same fictionalized value. There are two ways to perform consistent masking:

1. Wrap a `MaskingProvider` to a `LocallyPersistentMaskingProvider`. Following the example above:

```java
MaskingProvider maskingProvider = maskingProviderFactory.get("name");
MaskingProvider consistentMaskingProvider = new
					LocallyPersistentMaskingProvider(maskingProvider);
String maskedValue = consistentMaskingProvider.mask("John Smith");
```

2. Set `persistence.export` to `true` on the per field configuration. See the masking configuration page for more details




