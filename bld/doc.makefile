DOCS_BUILD_DIR := ./build/doc
MAIN_DOCS_BUILD_DIR := $(DOCS_BUILD_DIR)/main

.PHONY: main_doc
main_doc: DOXY_CFG := SOURCE_PATH=$(SOURCE_PATH) OUTPUT_DIR=$(MAIN_DOCS_BUILD_DIR)
main_doc: $(MAIN_DOCS_BUILD_DIR)
	$(DOXY_CFG) doxygen $(SOURCE_PATH)/doc/Doxyfile

$(MAIN_DOCS_BUILD_DIR):
	mkdir -p $(MAIN_DOCS_BUILD_DIR)

.PHONY: doc
doc: main_doc scala_doc
