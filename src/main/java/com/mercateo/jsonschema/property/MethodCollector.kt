package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class MethodCollector(
        private val annotationMapBuilder: AnnotationMapBuilder = AnnotationMapBuilder()
) : RawPropertyCollector {

    override fun <S> forType(genericType: GenericType<S>): Sequence<RawProperty<S, *>> {
        return sequenceOf(*genericType.declaredMethods)
                .filter { !it.isSynthetic }
                .filter { it.declaringClass != Any::class.java }
                .filter { it.returnType != Void.TYPE }
                .filter { it.parameterCount == 0 }
                .filter { !Modifier.isStatic(it.modifiers) }
                .map { mapRawDataProperty(method = it, genericType = genericType) }
    }

    private fun <S> mapRawDataProperty(method: Method, genericType: GenericType<S>): RawProperty<S, *> {
        val methodType = GenericType.ofMethod(method, genericType.type) as GenericType<Any>
        return RawProperty<S, Any>(getPropertyName(method),
                methodType,
                annotationMapBuilder.createMap(*method.annotations),
                { instance: S -> valueAccessor(method, instance) })
    }

    private fun getPropertyName(method: Method): String {
        val methodName = method.name
        if (methodName.startsWith("get") && Character.isUpperCase(methodName[3])) {
            return Character.toLowerCase(methodName[3]) + methodName.substring(4)
        }
        if (methodName.startsWith("is") && Character.isUpperCase(methodName[2])) {
            return Character.toLowerCase(methodName[2]) + methodName.substring(3)
        }
        return methodName
    }

    private fun <S> valueAccessor(method: Method, instance: S): Any? {
        return method.invoke(instance)
    }
}
