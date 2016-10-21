package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import java.lang.reflect.Method
import java.lang.reflect.Type

class MethodCollector(
        private val annotationMapBuilder: AnnotationMapBuilder = AnnotationMapBuilder()
) : RawPropertyCollector {

    override fun forType(genericType: GenericType<*>): Sequence<RawProperty> {
        return sequenceOf(*genericType.declaredMethods)
                .filter { !it.isSynthetic }
                .filter { it.declaringClass != Any::class.java }
                .filter { it.returnType != Void.TYPE }
                .filter { it.parameterCount == 0 }
                .map { mapRawDataProperty(it, genericType.type) }
    }

    private fun mapRawDataProperty(method: Method, type: Type): RawProperty {
        return RawProperty(getPropertyName(method),
                GenericType.ofMethod(method, type),
                annotationMapBuilder.createMap(*method.annotations),
                { instance: Any -> valueAccessor(method, instance) })
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

    private fun valueAccessor(method: Method, instance: Any): Any {
        return method.invoke(instance)
    }
}
