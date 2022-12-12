# HL7 masking


This example shows how to mask a value in the HL7 object model. 
Let's assume that the de-identification rule says that we need to hash the 
`/.MSH-3-1` element of a v2.3 HL7 object.

The notation of the element paths is based on the xref path defined here: [Terser documentation](https://hapifhir.github.io/hapi-hl7v2/base/apidocs/ca/uhn/hl7v2/util/Terser.html)

In the `toBeMasked` field, we create an entry with the key being the element path and the value being the masking provider we want to use for this elemement. 

```json
{
  
  "toBeMasked" : {
  	"/.MSH-3-1": "HASH"
  },

  "inputFormat": "HL7",
  "outputFormat": "HL7",	
	
  "_fields": {
  },
  "_defaults": {
  }
}
```

## Example of per-field configuration

According to the documentation, the default hash algorithm is SHA-256. If we want to change the behavior, let's say we want to use SHA-512, we can override the per-field configuration:


```json
{
  
  "toBeMasked" : {
  	"/.MSH-3-1": "HASH"
  },

  "inputFormat": "HL7",
  "outputFormat": "HL7",	
	
  "_fields": {
  		"/.MSH-3-1" : {
  			"hashing.algorithm.default": "SHA-512"
  		}
  },
  "_defaults": {
  }
}
```

## Masking through the API 

The HL7 record masking is being performed through the FormatProcessor object.
Initially, we need to get the processor for HL7 messages: 

```java
FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.HL7);
```

Once we get the processor, we can invoke the maskStream method to get the deidentified record back:

```java
void maskStream(InputStream dataset, PrintStream output,
                    ConfigurationManager configurationManager,
                    DataMaskingOptions dataMaskingOptions, Set<String> alreadyMaskedFields,
                    Map<ProviderType, Class<? extends MaskingProvider>> registerTypes) throws IOException;
```

The `ConfigurationManager` and `DataMaskingOptions` are instantiated based on the configuration contents, as described in the previous sections. The `dataset` parameter is the input stream to the record and the `output` is the print stream where the deidentified record will be printed. The `alreadyMaskedFields` parameter defines a list of fields that have been already masked externally and thus the processor will skip them. The `registerTypes` defines user-defined masking providers. 

A test example to de-identify a HL7 record using `hl7v23lookup.json` as a configuration file is shown below:

```java
@Test
    public void testHL7v23LookupTokens() throws Exception {

        String msg = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v23|T|2.3\r"
                + "PID|||7005728^^^TML^MR||RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value for Rachel Diamond";

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        ConfigurationManager configurationManager = ConfigurationManager.load(this.getClass().getResourceAsStream("/hl7v23lookup.json"));
        JsonNode maskingOptions = (new ObjectMapper()).readTree(this.getClass().getResourceAsStream("/hl7v23lookup.json"));

        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.HL7);
        formatProcessor.maskStream(inputStream, printStream, configurationManager, new DataMaskingOptions(maskingOptions), new HashSet<>(), null);
```