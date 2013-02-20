package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Extends another converter and tries to cast the returned type
 * 
 * @param <T> - type to cast the output to
 */
public class CastingConverter<T> implements Converter<T> {
	private final Class<T> outputType;
	private final Converter<?> baseConvertor;

	public CastingConverter(Class<T> outputType, Converter<?> baseConvertor) {
		this.outputType = outputType;
		this.baseConvertor = baseConvertor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T convert(Object value, T def) {
		Object val = CommonUtil.tryCast(baseConvertor.convert(value), this.getOutputType());
		if (val != null) {
			return (T) val;
		} else {
			return def;
		}
	}

	@Override
	public final T convert(Object value) {
		return convert(value, null);
	}

	@Override
	public Class<T> getOutputType() {
		return outputType;
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}

	@Override
	public boolean isRegisterSupported() {
		return true;
	}

	@Override
	public <K> ConverterPair<T, K> formPair(Converter<K> converterB) {
		return new ConverterPair<T, K>(this, converterB);
	}
}
