package com.rootcode.skapp.common.util.transformer;

public interface BaseTransformer<T, I> {

	I transform(T type);

}
