Gorm Logical Delete
===================

This plugin allows you to do a logical deletion of the domain classes.
The main intention of the plugin is to handle cases when certain entities cannot be physically removed from the database.

## How it works:

Most of the work is done using [AST transformations](http://groovy.codehaus.org/Compile-time+Metaprogramming+-+AST+Transformations).
For the desired class a new boolean property is added: deleted.
The GORM method __delete__ is modified to avoid the physical removal and just change the value of the _deleted_ property.
In addition to the AST transformations, a PreQueryEvent listener is added to filter queries in order to make the logical delete
logic transparent.

## How to use:

To provide logical deletion to a domain class you just need to add the __@GormLogicalDelete__ annotation to it.

```groovy
@GormLogicalDelete
class User {
   String lastName
   String firstName
   String nickName
    ...
}
```

To make a logical removal you just need to use the GORM method _delete_.

```groovy
user.delete()
```
which would be the equivalent to: (using default settings)

```groovy
user.deleted = true
user.save()
```
If you want to force a physical deletion to an annotated class, you have to add the __logicalDelete__ parameter in _false_ to the _delete_ method:

```groovy
user.delete(logicalDelete: false)
```

If you want to use the __logically__ deleted elements, you can use the _withDeleted_ method to execute a closure that includes the deleted items:

```groovy
user.withDeleted {
	def deletedUserList = User.list()
}
```

## Customization:

The plugin also supports customization of the property used and the deletedState value, in order to support current manual implementations.
For example, if your current implementation relies on the __enabled__ property being set to _false_ in order to logically delete an entity,
you would declare your annotation like this:


```groovy
@GormLogicalDelete(property = "enabled",deletedState = false)
class User {
   String lastName
   String firstName
   String nickName
    ...
}
```
It defaults to _proerty_ = __"deleted"__ and _deletedState_ = __true__

## IntelliJ code-completion:

There's a GDSL file bundled with code-completion for the _deletdState_ property and _withDeleted_ method.

## Credits:

This plugin is a fork from the [logical-delete](https://github.com/nanlabs/logical-delete) plugin. The original plugin relied on Hibernate filters and HawkEventing, and that wouldn't work for me since I needed it to stay 100% on GORM to be database-agnostic.
