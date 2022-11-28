package io.costax.examplesapi.beans.qualifier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Qualifier("important")
@Component
public class XmlDocumentSerializerImpl implements DocumentSerializer {
    @Override
    public String serialize(final String id) {
        return "<document><id>" + id + "</id></document>";
    }

    @Override
    public String contentType() {
        return "application/xml";
    }
}
