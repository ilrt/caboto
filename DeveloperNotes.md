Notes for caboto developers.

# Merging from branches #

For some reason this never seems as simple as the svn documentation claims. The following seems to work for me:

In the branch:

```
# svn log --stop-on-copy
```

That will let you know which revision the branch departed from trunk (I'll assume trunk here).

```
# svn merge -r REV_BRANCHED_AT <url of trunk>
```

It's always worth trying it first using --dry-run.

If everything is working try merging to trunk:

```
# svn merge -r REV_BRANCHED_AT . <url of trunk>
```