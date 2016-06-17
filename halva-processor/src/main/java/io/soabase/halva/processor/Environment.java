package io.soabase.halva.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Environment
{
    void error(Element element, String message);

    void log(String message);

    void debug(String message);

    Elements getElementUtils();

    Types getTypeUtils();

    String getPackage(TypeElement element);

    String getCaseClassSimpleName(TypeElement element, AnnotationReader annotationReader);

    Collection<Modifier> getModifiers(TypeElement element);

    void createSourceFile(String packageName, ClassName templateQualifiedClassName, ClassName generatedQualifiedClassName, String annotationType, TypeSpec.Builder builder, TypeElement element);

    Optional<List<TypeVariableName>> addTypeVariableNames(TypeSpec.Builder builder, TypeElement element);
}
