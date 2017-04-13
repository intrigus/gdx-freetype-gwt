# gdx-freetype-gwt

You ever wanted to use freetype on the web version of your game but couldn't? Now you can!

# How-To
1. Go to your `GdxDefinition.gwt.xml` in your `html` subproject

Add 

`<inherits name="com.badlogic.gdx.graphics.g2d.freetype.freetype-gwt" />`

after 

`<inherits name='com.badlogic.gdx.backends.gdx_backends_gwt' />`

2. Change your `build.gradle` of the `html` subproject

Add 
````
 compile "com.github.intrigus.gdx-freetype-gwt:gdx-freetype-gwt:0.0.2-SNAPSHOT"
 compile "com.github.intrigus.gdx-freetype-gwt:gdx-freetype-gwt:0.0.2-SNAPSHOT:sources"
````

3. Modify your `HtmlLauncher.java` (or if it's not named so, modify the class in your `html` project that extends `GwtApplication`)

Add
````java
@Override
public void onModuleLoad () {
	FreetypeInjector.inject(new OnCompletion() {
		public void run () {
			// Replace HtmlLauncher with the class name
			// If your class is called FooBar.java than the line should be FooBar.super.onModuleLoad();
			HtmlLauncher.super.onModuleLoad();
		}
	});
}
````

4. Profit and Enjoy
