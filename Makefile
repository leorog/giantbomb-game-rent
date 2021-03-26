run:
	$(MAKE) -C client release
	rsync -r client/assets/ api/resources/public/
	$(MAKE) -C api release
	$(MAKE) -C api run
