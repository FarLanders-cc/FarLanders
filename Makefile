
# Makefile: convenience targets for common maintenance and dev scripts

.PHONY: help preview quick-preview convert-changelog clean-empty-files docs test build javadoc preview-exporter

SCRIPTS_DIR := ./scripts

help:
	@printf "Available targets:\n"
	@printf "  help                 - show this help\n"
	@printf "  preview              - run full docs preview (runs preview-docs.sh)\n"
	@printf "  quick-preview        - run quick-preview.sh (faster preview)\n"
	@printf "  convert-changelog    - convert CHANGELOG.md -> docs/changelog.html\n"
	@printf "  clean-empty-files    - list empty files (pass --force to delete)\n"
	@printf "  docs                 - regenerate docs (runs convert-changelog)\n"
	@printf "  test                 - run project tests via Gradle\n"
	@printf "  preview-exporter     - run the headless FullChunkExporter via Gradle (./gradlew runFullChunkExporter)\n"

preview:
	@echo "Running full docs preview..."
	@$(SCRIPTS_DIR)/preview-docs.sh

quick-preview:
	@echo "Running quick preview..."
	@$(SCRIPTS_DIR)/quick-preview.sh

convert-changelog:
	@echo "Converting CHANGELOG.md -> docs/changelog.html"
	@$(SCRIPTS_DIR)/convert-changelog.sh

clean-empty-files:
	@echo "Find empty files (dry-run). To actually delete, run: make clean-empty-files FORCE=1 or pass --force to the script."
	@$(SCRIPTS_DIR)/clean-empty-files.sh

clean-force:
	@$(SCRIPTS_DIR)/clean-empty-files.sh --force
	@rm -rf build dist public bin

docs: convert-changelog
	@echo "Docs updated. Run 'make preview' to preview locally."

preview-exporter:
	@echo "Running headless FullChunkExporter (Gradle task runFullChunkExporter)..."
	@if [ -n "$(OUTPUT_DIR)" ]; then \
		echo "Using OUTPUT_DIR=$(OUTPUT_DIR)"; \
		./gradlew -PexporterOutput="$(OUTPUT_DIR)" runFullChunkExporter $(ARGS); \
	else \
		./gradlew runFullChunkExporter $(ARGS); \
	fi

# Convenience wrappers for Gradle tasks (if you prefer make)
test:
	@./gradlew test

build:
	@./gradlew build

javadoc:
	@./gradlew javadoc

