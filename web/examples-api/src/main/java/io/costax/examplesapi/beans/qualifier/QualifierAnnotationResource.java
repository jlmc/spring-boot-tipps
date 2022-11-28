package io.costax.examplesapi.beans.qualifier;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping(value = "/qualifier-annotation")
public class QualifierAnnotationResource {

    @Autowired
    @Qualifier("normal")
    DocumentSerializer serializer;

    @Autowired
    BeanFactory beanFactory;

    @GetMapping
    public ResponseEntity<String> hello(@RequestParam(name = "id", required = false) String id) {
        String s = Optional.ofNullable(id).orElse("2.18. Bean disambiguation with @Qualifier");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(serializer.contentType()))
                .body(serializer.serialize(s));
    }

    @GetMapping("/{type}")
    public ResponseEntity<?> resolveDynamic(
            @PathVariable("type") String type,
            @RequestParam(name = "id", required = false) String id) {
        String s = Optional.ofNullable(id).orElse("2.18. Bean disambiguation with @Qualifier");

        //final DocumentSerializer important = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, DocumentSerializer.class, "important");
        final DocumentSerializer dynamicQualifierBean = resolveDynamicTheQualifierBean(type);

        if (dynamicQualifierBean == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dynamicQualifierBean.contentType()))
                .body(dynamicQualifierBean.serialize(s));
    }

    private DocumentSerializer resolveDynamicTheQualifierBean(final String type) {
        try {
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, DocumentSerializer.class, type);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

}
