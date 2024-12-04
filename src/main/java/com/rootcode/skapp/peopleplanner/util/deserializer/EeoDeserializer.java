package com.rootcode.skapp.peopleplanner.util.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.rootcode.skapp.common.exception.ModuleException;
import com.rootcode.skapp.peopleplanner.constant.PeopleMessageConstant;
import com.rootcode.skapp.peopleplanner.type.EEO;

import java.io.IOException;

public class EeoDeserializer extends StdDeserializer<EEO> {

	public EeoDeserializer() {
		super(EEO.class);
	}

	@Override
	public EEO deserialize(JsonParser p, DeserializationContext ctxt) throws ModuleException, IOException {
		JsonNode jsonNode = p.readValueAsTree();
		String value = jsonNode.asText().trim();

		if (jsonNode.isNull() || jsonNode.isMissingNode() || value.isEmpty()) {
			return null;
		}

		try {
			return EEO.valueOf(value.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_VALUE_FOR_EEO_ENUM,
					new String[] { value });
		}
	}

}
