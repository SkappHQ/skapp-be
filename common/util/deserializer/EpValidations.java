package com.skapp.enterprise.common.util.deserializer;


import com.skapp.community.common.exception.ValidationException;
import com.skapp.enterprise.common.constant.EPCommonMessageConstant;
import com.skapp.enterprise.esignature.payload.request.EnvelopeDetailDto;
import com.skapp.enterprise.esignature.payload.request.FieldDto;
import com.skapp.enterprise.esignature.payload.request.RecipientDto;
import com.skapp.enterprise.esignature.type.EnvelopeStatus;
import com.skapp.enterprise.esignature.type.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EpValidations {

    public static void validateEnvelopeDetails(EnvelopeDetailDto envelopeDetailDto) {
        if (envelopeDetailDto.getEnvelopeStatus() == EnvelopeStatus.DRAFT) {
            return;
        }

        if (envelopeDetailDto.getName() == null || envelopeDetailDto.getName().isBlank()) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_ENVELOPE_TITLE);
        }

        if (envelopeDetailDto.getMessage() == null || envelopeDetailDto.getMessage().isBlank()) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_ENVELOPE_MESSAGE);
        }

        if (envelopeDetailDto.getSubject() == null || envelopeDetailDto.getSubject().isBlank()) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_ENVELOPE_SUBJECT);
        }

        if (envelopeDetailDto.getExpireAt() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_ENVELOPE_EXPIRES_AT);
        }

        if (envelopeDetailDto.getDocumentIds() == null || envelopeDetailDto.getDocumentIds().isEmpty()) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_ENVELOPE_DOCUMENT);
        }

        if (envelopeDetailDto.getRecipients() == null || envelopeDetailDto.getRecipients().isEmpty()) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_ENVELOPE_RECIPIENT);
        } else {
            envelopeDetailDto.getRecipients().forEach(EpValidations::validateRecipientDto);
        }
    }

    private static void validateRecipientDto(RecipientDto recipientDto) {
        if (recipientDto.getId() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_RECIPIENT_ID);
        }

        if (recipientDto.getMemberRole() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_RECIPIENT_ROLE);
        }

        if (recipientDto.getSigningOrder() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_RECIPIENT_ORDER);
        }

        if (recipientDto.getFields() == null || recipientDto.getFields().isEmpty()) {
            if (recipientDto.getMemberRole() == MemberRole.SINGER) {
                throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_RECIPIENT_ORDER);
            }
        } else {
            recipientDto.getFields().forEach(EpValidations::validateFieldDto);
        }
    }

    private static void validateFieldDto(FieldDto fieldDto) {
        if (fieldDto.getType() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_FIELD_TYPE);
        }

        if (fieldDto.getDocumentId() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_FIELD_DOCUMENT);
        }

        if (fieldDto.getPageNumber() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_FIELD_PAGE_NUMBER);
        }

        if (fieldDto.getXPosition() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_FIELD_X_POSITION);
        }

        if (fieldDto.getYPosition() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_FIELD_Y_POSITION);
        }

        if (fieldDto.getRecipientMail() == null) {
            throw new ValidationException(EPCommonMessageConstant.EP_COMMON_ERROR_VALIDATION_ENTER_FIELD_RECIPIENT);
        }
    }
}
