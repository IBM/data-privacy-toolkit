## Identify a value based on the available providers

In the following example, we will try to identify the type for the value `John`. We are looping through all the available identifiers, as returned by `IdentifierFactory.availableIdentifiers()` and we invoke the `.isOfThisType(value)` for each identifier. On a successful match we print the type name. 

```java
import com.ibm.research.drl.prima.providers.ProviderType;
import com.ibm.research.drl.prima.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.prima.providers.identifiers.Identifier;

public void testIdentifyValue() {

    //retrieve a list of available identifiers
    Collection<Identifier> identifiers = IdentifierFactory.availableIdentifiers();

    String value = "John";

    //call the identifiers and see if we have a match for a value
    for(Identifier identifier: identifiers) {
        boolean match = identifier.isOfThisType(value);

        if (match == true) {
            System.out.println("identifier matched: " + identifier.getType().name());
        }
    }
}
```
 
## Identify the types of attributes for various input formats

The basic principle is to create a `FormatProcessor` for the given format and then call

```java
Map<String, Map<String, List<IdentifiedType>>> 
	identifyTypesStream(InputStream input, DataTypeFormat inputFormatType, 
			DatasetOptions datasetOptions, Collection<Identifier> identifiers, int firstN);
```

* `input` is an InputStream to the data
* `inputFormatType` is an enumeration of the formats that support type identification. The list of formats are CSV, JSON, DICOM, XLS, XLSX, XML, PARQUET, VCF, JDBC. 
* `datasetOptions` contains dataset-specific options. It should be an instance of CSVDatasetOptions in the case of CSV else it should be `null`
* `identifiers` is a collection of Identifier objects to run on top of the data
* if `firstN` has a value greater than zero then only this amount of records will be processed only

A `FormatProcessor` object can be instantiated from the FormatProcessorFactory's `getProcessor(DataTypeFormat datatypeFormat)`

### CSV

```java
public void testDatasetIdentification(InputStream is, boolean hasHeader) throws Exception {
    DatasetOptions datasetOptions = new CSVDatasetOptions(hasHeader, ',', '"', false);
    
    FormatProcessor csvProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.CSV);
    
    Collection<Identifier> defaultIdentifiers = IdentifierFactory.defaultIdentifiers();
    
    Map<String, Map<String, List<IdentifiedType>>> results = 
    				csvProcessor. identifyTypesStream(is, DataTypeFormat.CSV, datasetOptions, 
    				defaultIdentifiers, -1);
    		
	// print results here
}
```

### JSON, DICOM, XLS, XLSX, XML, PARQUET, VCF

Similar to CSV case but changing the DataTypeFormat to the factory call as well as the dataset options should be set to `null`. The `InputStream` should be a regular input stream to the contents of the data
(like a FileInputStream).


### JDBC

```java
private static Connection getConnection() throws ClassNotFoundException, SQLException {
        String DBUrl      = "jdbc:postgresql://localhost/test";
        String DBUsername = "postgres";
        String DBPassword = ""; // Need password

        //Class.forName(DBDriver);
        return DriverManager.getConnection(DBUrl, DBUsername, DBPassword);
}

public void testDatabase() {
	String schemaName = "s1";
	String tableName = "people";
	
	InputStream jdbcStream  = new ResultSetInputStream(getConnection(), schemaName, tableName);
	
	FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.JDBC);
	
	Map<String, Map<String, List<IdentifiedType>>> results = 
		formatProcessor. identifyTypesStream(jdbcStream, DataTypeFormat.JDBC, null,
		defaultIdentifiers, -1);
		
	//print results here
}
```


## Register a user-defined regex-based identifier

The toolkit allows the registration of custom regex-based identifiers. In the following example we will create a regex-based identifier that will match the regex `^foobar$` and will report the matches as being of type `FOOBAR`

We first create a list that contains the regular expressions

```java
Collection<String> patterns = Arrays.asList(new String[] {"^foobar$"});
```
We create a new PluggableRegexIdentifier

The first argument is the name of the provider type (can be anything)
The second argument is a list of possible column names
The third argument is the list of regex patterns created in the first step
The last argument is the value class, possible values are TEXT, NUMERIC, DATE, LOCATION, UNKNOWN (the value class is used for relationship identification and compound masking)

```java
Identifier pluggableRegexIdentifier = new PluggableRegexIdentifier("FOOBAR", Arrays.asList(new String[] {"foo"}), patterns, ValueClass.TEXT);
```
We register the identifier through the IdentifierFactory
```java
IdentifierFactory.registerIdentifier(pluggableRegexIdentifier);
```
After the registration the identifier is available through `IdentifierFactory.availableIdentifiers()` or can be invoked directly.
 
## Register a user-defined dictionary-based identifier

The toolkit allows the registration of custom dictionary-based identifiers.
In the following example we will create a regex-based identifier that will match the terms `term1` and `term2` and will report the matches as being of type `FOOBAR`

```java
//we first create a list that contains the terms for lookup
Collection<String> terms = Arrays.asList(new String[] {"term1", "term2"});
```
We create a new PluggableLookupIdentifier. 
The first argument is the name of the provider type (can be anything)
The second argument is a list of possible column names 
The third argument is the list of terms created in the first step
The fourth argument is a boolean flag that indicates if we want to ignore case or not
The last argument is the value class, possible values are TEXT, NUMERIC, DATE, LOCATION, UNKNOWN (the value class is used for relationship identification and compound masking)
```java
Identifier pluggableLookupIdentifier = new PluggableLookupIdentifier("FOOBAR", Arrays.asList(new String[] {"foo"}), terms, true, ValueClass.TEXT);
```

We register the identifier through the IdentifierFactory
```java
IdentifierFactory.registerIdentifier(pluggableLookupIdentifier);
```
After the registration the identifier is available through `IdentifierFactory.availableIdentifiers()` or can be invoked directly.