[![Build Status](https://travis-ci.org/saboya/gorm-logical-delete.svg?branch=master)](https://travis-ci.org/saboya/gorm-logical-delete) [![Coverage Status](https://coveralls.io/repos/saboya/gorm-logical-delete/badge.svg?branch=master)](https://coveralls.io/r/saboya/gorm-logical-delete?branch=master) [![Project Status](https://stillmaintained.com/saboya/gorm-logical-delete.png)](https://stillmaintained.com/saboya/gorm-logical-delete)
Gorm Logical Delete
===================

This plugin allows you to do a logical deletion of the domain classes.
The main intention of the plugin is to handle cases when certain entities cannot be physically removed from the database.

## How it works:

A boolean property is injected in the annotated domain class using [AST transformations](http://groovy.codehaus.org/Compile-time+Metaprogramming+-+AST+Transformations). This property is used to track the logical _delete state_ of the entity. The name and deleted state value can be customized.

The GORM method __delete()__ is modified to avoid the physical removal and just change the value of the _delete state_ property.

To handle read queries, a __PreQueryEvent__ listener is added in order to make the logical delete logic transparent, adding another criteria to the query so it doesn't match deleted entities.

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
It defaults to _property_ = __"deleted"__ and _deletedState_ = __true__

## IntelliJ code-completion:

There's a GDSL file bundled with code-completion for the _deletdState_ property and _withDeleted_ method.

## Credits:

This plugin is a fork from the [logical-delete](https://github.com/nanlabs/logical-delete) plugin. The original plugin relied on Hibernate filters and HawkEventing, and that wouldn't work for me since I needed it to stay 100% on GORM to be database-agnostic.
