## Info
Arrays are supported by the checker, but only of primitive types and JsonObjects. The code does not support multidimensional arrays.

## Usage
A quick insight in how to use this resource:
Let us assume you want to check if a given JsonObject complies with a certain structure you want. For this, I've written a small demo JSON file:
```json
{
  "meta_data": {
    "parser_version": "1.0"
  },
  "weapon_data": {
    "id": 100,
    "name": "Sword",
    "attack_speed": 1.8,
    "range": 1
  }
}
```

This is how I expect/want the JSON to look like. But rarely can you assume data is provided correctly.  
Hence we would be interested in checking if any given JsonObject complies with our structure to make parsing easier. To achieve this you can do the following:
```java
final JsonObjFormatBuilder jsonStructure = JsonFormatBuilder.createObjBuilder()
                .setKeyAsObj(
                        "meta_data",
                        JsonFormatBuilder.createObjBuilder()
                                .setKeyAsStr("parser_version").build()
                ).build()
                .setKeyAsObj(
                        "weapon_data",
                        JsonFormatBuilder.createObjBuilder()
                                .setKeyAsInt("id").build()
                                .setKeyAsStr("name").build()
                                .setKeyAsUFlt("attack_speed").build()
                                .setKeyAsUInt("range").build()
                ).build();
```

Now simply use the `#checkFormat` function with a JsonObject to validate the structure.
```java
final JsonObjFormatBuilder.JsonCheckResult jsonCheckResult = jsonStructure.checkFormat(jsonObject);
```

We receive a `JsonCheckResult` from which you can check if the JSON is formatted correctly using `#isInFormat`.  
If the checker comes across any errors it will log them in a `Map<String, JsonFormatCheckError>`. To get these errors use `#getErrorMsgs`.  
The code does a "deep check" and tries to check as far as it can. Therefore the Map should contain all known errors in the JSON at once.
