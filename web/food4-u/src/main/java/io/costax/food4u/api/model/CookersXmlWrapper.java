package io.costax.food4u.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import io.costax.food4u.domain.model.Cooker;
import lombok.Data;
import lombok.NonNull;

import java.util.List;


/*
 * @JacksonXmlRootElement é uma alternativa à @JsonRootName e
 * @JacksonXmlProperty à @JsonProperty.
 *
 * A diferença é que as anotações iniciadas com @JacksonXml só afetam
 * a serialização em XML. Já as anotações iniciadas com @Json
 * afetam tanto a serialização JSON como também XML.
 */

@JsonRootName("cookers")
//@JacksonXmlRootElement(localName = "cookers")
@Data
public class CookersXmlWrapper {

    @JsonProperty("cooker")
//	@JacksonXmlProperty(localName = "cookers")
    @JacksonXmlElementWrapper(useWrapping = false)
    @NonNull
    private List<Cooker> cookers;


    public CookersXmlWrapper(final List<Cooker> cookers) {
        this.cookers = cookers;
    }
}
