## Loading localized resources programmatically

In our example, let's assume that we need to register a new localization for races/ethnicities. The dictionary content is located in file `/tmp/races_fantasy.csv` and contains two entries: `Goblin` and `Elf`. The new locale is called `fantasy`. It is very important that the registration process needs to be done BEFORE the instantiation of masking providers

```java
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.providers.masking.RaceEthnicityMaskingProvider;

 public void testNewExternalResources() throws Exception {
        //this is the file containing the dictionary content
        String filePath = "/identifier/races_fantasy.csv"; 
        
        //this is the locale code
        String newCountryCode = "fantasy";
        
        final LocalizationManager manager = LocalizationManager.getInstance();

		 //we first register the country code
        manager.registerCountryCode(newCountryCode);

        //each resource loaded is assigned to a country code and a Resource object. 
        //See the Section below for the list of resource names
        manager.registerResource(Resource.RACE_ETHNICITY, newCountryCode, filePath);
       
        //AFTER the resource is register, we can start instantiating 
        //identifiers and masking providers
        
        RaceEthnicityMaskingProvider maskingProvider = new RaceEthnicityMaskingProvider();
        //this will randomly print either Goblin or Elf
        System.out.println(maskingProvider.mask("Goblin"));
    }
```

### List of resources

See common/localization-resource-list.md

#### Notes

For proper output format, the JVM needs to be set up to use UTF-8

Two options:

a) use `-Dfile.encoding=UTF-8` option when invoking the toolkit

b) set the value of LC_ALL environmental variable in the shell where the toolkit will run: `export LC_ALL="en_US.UTF-8"`
