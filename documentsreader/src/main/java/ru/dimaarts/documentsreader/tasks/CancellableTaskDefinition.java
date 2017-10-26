package ru.dimaarts.documentsreader.tasks;

public interface CancellableTaskDefinition <Params, Result>
{
	Result doInBackground(Params ... params);
	void doCancel();
	void doCleanup();
}
