package com.github.lukfor.commands;

import java.util.concurrent.Callable;

import com.github.lukfor.App;

import picocli.CommandLine.Command;

@Command(name = "version", version = App.VERSION)
public class VersionCommand implements Callable<Integer> {

	public Integer call() throws Exception {

		return 0;
	}
}
