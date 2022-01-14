# Table of contents

1. [Endpoints](#endpoints)
    - [`GET` health status](#get_health)
    - [`GET` all users](get_all_users)
    - [`POST` user](#post_user)
    - [`GET` user](#get_user)
    - [`GET` all workflowlists](#get_all_workflowlists)
    - [`POST` workflowlist](#post_workflowlist)
    - [`DELETE` workflowlist](#delete_workflowlist)
    - [`PUT` workflowlist](#put_workflowlist)
    - [`PUT` workflowlist resource](#put_workflowlist_resource)
    - [`PUT` workflowlist type](#put_workflowlist_type)
    - [`PUT` workflowlist position](#put_workflowlist_position)
    - [`PUT` workflowlist parent](#put_workflowlist_parent)
    - [`GET` workflowlist scheduling](#get_workflowlist_scheduling)
2. [Authorisation](#authorisation)
3. [Enums](#enums)

# Endpoints <a name="endpoints"></a>

### Check if server is healthy <a name="get_health"></a>

```
GET /health
```

### Get all users <a name="get_all_users"></a>

```
GET /user
```

### Create a new user <a name="post_user"></a>

```
POST /user
```

#### Parameters

| Name       | Type   | In   | Description  |
|------------|--------|------|--------------|
| `username` | string | body | __Required__ |

### Get a single user <a name="get_user"></a>

```
GET /user/{userApiId}
```

#### Parameters

| Name        | Type   | In   | Description  |
|-------------|--------|------|--------------|
| `userApiId` | string | path | __Required__ |

### Get all workflowlists <a name="get_all_workflowlists"></a>

```
GET /workflowlist
```

#### Parameters

| Name        | Type   | In    | Description                         |
|-------------|--------|-------|-------------------------------------|
| `userApiId` | string | query | Only get of lists of specified user |

### Create a new workflowlist <a name="post_workflowlist"></a>

```
POST /workflowlist
```

#### Parameters

| Name                        | Type                            | In   | Description  |
|-----------------------------|---------------------------------|------|--------------|
| `title`                     | string                          | body | __Required__ |
| `description`               | string                          | body |              |
| `listType`                  | WorkflowListType                | body | __Required__ |
| `parentApiId`               | boolean                         | body |              |
| `isTemporalConstraintBoard` | string                          | body |              |
| `children`                  | array(CreateWorkflowListEntity) | body | __Required__ |

### Delete a workflowlist <a name="delete_workflowlist"></a>

```
DELETE /workflowlist/{workflowlistId}
```

#### Parameters

| Name             | Type   | In   | Description  |
|------------------|--------|------|--------------|
| `workflowlistId` | string | path | __Required__ |

### Update a workflowlist <a name="put_workflowlist"></a>

```
PUT /workflowlist/{workflowlistId}
```

#### Parameters

| Name                        | Type    | In   | Description  |
|-----------------------------|---------|------|--------------|
| `workflowlistId`            | string  | path | __Required__ |
| `newTitle`                  | string  | body | __Required__ |
| `newDescription`            | string  | body |              |
| `isTemporalConstraintBoard` | boolean | body |              |

### Update the resources of a workflowlist <a name="put_workflowlist_resource"></a>

```
PUT /workflowlist/{workflowlistId}/resource
```

#### Parameters

| Name             | Type                         | In   | Description  |
|------------------|------------------------------|------|--------------|
| `workflowlistId` | string                       | path | __Required__ |
| `numeric`        | array(NumericResourceEntity) | body |              |
| `textual`        | array(TextualResourceEntity) | body |              |
| `temporal`       | TemporalResourceEntity       | body |              |
| `user`           | UserResourceEntity           | body |              |

#### NumericResourceEntity

| Name    | Type   | Description  |
|---------|--------|--------------|
| `label` | string | __Required__ |
| `value` | float  | __Required__ |

#### TextualResourceEntity

| Name    | Type   | Description  |
|---------|--------|--------------|
| `label` | string | __Required__ |
| `value` | string |              |

#### UserResourceEntity

| Name       | Type   | Description |
|------------|--------|-------------|
| `username` | string |             |

#### TemporalResourceEntity

| Name                | Type | Description |
|---------------------|------|-------------|
| `startDate`         | date |             |
| `endDate`           | date |             |
| `durationInMinutes` | long |             |

### Convert a workflowlist to a new type <a name="put_workflowlist_type"></a>

```
PUT /workflowlist/{workflowlistId}/type
```

#### Parameters

| Name             | Type             | In   | Description  |
|------------------|------------------|------|--------------|
| `workflowlistId` | string           | path | __Required__ |
| `newListType`    | WorkflowListType | body | __Required__ |

### Change the position of a workflowlist within its parent <a name="put_workflowlist_position"></a>

```
PUT /workflowlist/{workflowlistId}/position
```

#### Parameters

| Name             | Type    | In   | Description  |
|------------------|---------|------|--------------|
| `workflowlistId` | string  | path | __Required__ |
| `newPosition`    | integer | body | __Required__ |

### Move a workflowlist to a new parent <a name="put_workflowlist_parent"></a>

```
PUT /workflowlist/{workflowlistId}/parent
```

#### Parameters

| Name             | Type    | In   | Description  |
|------------------|---------|------|--------------|
| `workflowlistId` | string  | path | __Required__ |
| `newParentApiId` | string  | body |              |
| `newPosition`    | integer | body |              |

### Get a scheduling proposal for a workflowlist from the symbolic intelligence <a name="get_workflowlist_scheduling"></a>

```
GET /workflowlist/{workflowlistId}/scheduling
```

#### Parameters

| Name             | Type   | In   | Description  |
|------------------|--------|------|--------------|
| `workflowlistId` | string | path | __Required__ |

# Authorization <a name="authorization"></a>

All requests to `/workflowlist` require the UUID of an active user as an `Authorization` header. Otherwise, the API will
return `401 Unauthorized`.

# Enums <a name="enums"></a>

### WorkflowListType

- `ITEM`
- `BOARD`
- `LIST`

###
