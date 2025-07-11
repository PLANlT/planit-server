package com.planit.planit.agreement.service;

import java.util.Map;

public interface AgreementService {
    /**
 * Retrieves all available terms URLs organized by category.
 *
 * @return a map where each key represents a category and the value is another map of term names to their corresponding URLs.
 */
    Map<String, Map<String, String>> getAllTermsUrls();
}
