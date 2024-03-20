# JsonStructureChecker
*JsonStructureChecker* (JSC) is a small library to check if a given JSON complies with a certain structure. This can be useful when you want to check if a JSON is formatted correctly before parsing it.  
*JSC* provides rich error messages to help you understand what is wrong with the JSON.

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
Hence we would be interested in checking if any given JSON complies with our structure to make parsing easier. To achieve this you can do the following:
```java
final JsonSchema schema = JsonSchemaBuilder.createSchema(TagObj.builder()
        .setKeyAs("meta_data", TagObj.builder()
                .setKeyAs("parser_version", TagType.STR)
                .build())
        .setKeyAs("weapon_data", TagObj.builder()
                .setKeyAs("id", TagType.UUID)
                .setKeyAs("name", TagType.STR)
                .setKeyAs("attack_speed", TagType.U_FLT)
                .setKeyAs("range", TagType.U_INT)
                .build())
);
```

Now simply use the `#check` function with the JSON to validate the structure.
```java
final CheckResult result = schema.check(json);
```

We receive a `CheckResult` from which you can check if the JSON is formatted correctly using `#isValid`.  
If the checker comes across any errors it will log them in a `Map<String, JsonFormatCheckError>`. To get these errors use `#getErrorMsgs`.  
The code does a "deep check" and tries to check as far as it can. Therefore, the Map should contain all known errors in the JSON at once.

## Data Types
The following data types are supported by *JSC*:
- `TagType.STR` - String
- `TagType.CHAR` - Character
- `TagType.INT` - Integer
- `TagType.U_INT` - Unsigned Integer (Only positive integers)
- `TagType.S_INT` - Signed Integer (Only negative integers)
- `TagType.FLT` - Float
- `TagType.U_FLT` - Unsigned Float (Only positive floats)
- `TagType.S_FLT` - Signed Float (Only negative floats)
- `TagType.BOOL` - Boolean
- `TagType.UUID` - UUID
- `TagType.ENUM` - Enum
- `TagType.ARR` - Array
- `TagType.OBJ` - Object
- `TagType.ANY` - Any type

The following two data types are "special" and have very special behaviors:
- `TagType.GROUP` - Group
- `TagType.CONDITIONAL` - Conditional

Almost all data types have a "builder" method to create them, since some custom parameters can be set.  
The class of the data type is generally named: "Tag\<Type>".

### Group
The `TagType.GROUP` is a special data type that allows to "compress" larger structures into a single group name.
When creating a `TagGroup` you specify which group should be inserted via the group name.  
The main purpose of this data type is to allow the group tag to be used in itself, allowing for recursive structures. If you do this, make sure to set the group as "optional", otherwise it will always throw an error!  
A group tag is defined/registered by the `JsonSchemaBuilder` via the `#addGroup` method. Using this you define which `JsonTag` will be inserted in place of the `TagGroup`.

### Conditional
The `TagType.CONDITIONAL` is a special data type that allows to check if the value (`referenceValue`) at a specified key (`referenceKey`) are the same.
If this is the case, the `TagConditional` will expect a certain structure (`tagFormat`) at the `destinationKey`.  
This is a very confusing Tag, but can be very useful in some cases. In short, it does the following:
- Check if the value at `referenceKey` is equal to a predefined `referenceValue`
- If it is, check if the structure at `destinationKey` complies with `tagFormat`
- If it does, the `TagConditional` is valid 

This allows the value of a key to determine the structure of the JSON. It is best suited for cases where you check if
the value of a key is some Enum, if so, additional information can be provided.  
`TagConditional` does not possess an "optional" statement, instead use `#setNotRequired`.