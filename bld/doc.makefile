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

.PHONY: doc_ditaa
doc_ditaa: DITAA := /home/igor/tool/ditaa/ditaa
doc_ditaa: HTML_FILES = $(shell find $(abspath $(DOCS_BUILD_DIR)) -type f -name '*.html')
doc_ditaa: FILES_WITH_DIAGRAMS = $(shell grep -l textdiagram $(HTML_FILES))
doc_ditaa: doc
	@echo Generate DITAA diagrams
	@- $(foreach file,$(FILES_WITH_DIAGRAMS), \
		cd $(dir $(file)) && $(DITAA) --html $(file) -o $(file) ; \
	)
