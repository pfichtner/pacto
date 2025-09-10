package com.github.pfichtner.pacto;

class PactoSettingsImpl implements PactoSettings {

	private boolean strict = true;

	@Override
	public PactoSettings lenient() {
		return lenient(true);
	}

	@Override
	public PactoSettings lenient(boolean lenient) {
		strict = !lenient;
		return this;
	}

	boolean isStrict() {
		return strict;
	}

}
