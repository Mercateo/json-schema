package com.mercateo.jsonschema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.validation.constraints.NotNull;

public class Polymorphism {
    public Contact contact;
    public String name;
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "email", value = EmailContact.class),
        @JsonSubTypes.Type(name = "fax", value = FaxContact.class)
})
interface Contact {}

class EmailContact implements Contact {
    @NotNull
    public String emailAddress;
}
class FaxContact implements Contact {
    @NotNull
    public String faxNumber;
    public String personName;
}

