# Table of contents

- [Authorization](#authorization)
- [Endpoints](#endpoints)
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
- [Enums](#enums)

# Authorization <a name="authorization"></a>

All requests to `/workflowlist/...` require the UUID of an active user as an `Authorization` header. Otherwise, the API will
return `401 Unauthorized`.

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

#### JSON body parameters

| Name       | Type   | Required | Description |
|------------|--------|----------|-------------|
| `username` | string | yes      |             |

### Get a single user <a name="get_user"></a>

```
GET /user/:userApiId
```

### Get all workflowlists <a name="get_all_workflowlists"></a>

```
GET /workflowlist
```

#### URI parameters

| Name        | Type   | Required | Description    |
|-------------|--------|----------|----------------|
| `userApiId` | string | no       | Filter by user |

### Create a new workflowlist <a name="post_workflowlist"></a>

```
POST /workflowlist
```

#### JSON body parameters

| Name                        | Type                            | Required | Description                  |
|-----------------------------|---------------------------------|----------|------------------------------|
| `title`                     | string                          | yes      |                              |
| `description`               | string                          | no       |                              |
| `listType`                  | WorkflowListType                | yes      |                              |
| `parentApiId`               | boolean                         | no       |                              |
| `isTemporalConstraintBoard` | string                          | no       |                              |
| `children`                  | array(CreateWorkflowListEntity) | yes      | Leave empty for no children. |

### Delete a workflowlist <a name="delete_workflowlist"></a>

```
DELETE /workflowlist/:workflowlistApiId
```

### Update a workflowlist <a name="put_workflowlist"></a>

```
PUT /workflowlist/:workflowlistApiId
```

#### JSON body parameters

| Name                        | Type    | Required | Description |
|-----------------------------|---------|----------|-------------|
| `newTitle`                  | string  | yes      |             |
| `newDescription`            | string  | no       |             |
| `isTemporalConstraintBoard` | boolean | no       |             |

### Update the resources of a workflowlist <a name="put_workflowlist_resource"></a>

```
PUT /workflowlist/:workflowlistApiId/resource
```

#### JSON body parameters

| Name             | Type                         | Required | Description  |
|------------------|------------------------------|----------|--------------|
| `numeric`        | array(NumericResourceEntity) | no       |              |
| `textual`        | array(TextualResourceEntity) | no       |              |
| `temporal`       | TemporalResourceEntity       | no       |              |
| `user`           | UserResourceEntity           | no       |              |

#### NumericResourceEntity

| Name    | Type   | Required | Description |
|---------|--------|----------|-------------|
| `label` | string | yes      |             |
| `value` | float  | yes      |             |

#### TextualResourceEntity

| Name    | Type   | Required | Description |
|---------|--------|----------|-------------|
| `label` | string | yes      |             |
| `value` | string | no       |             |

#### UserResourceEntity

| Name       | Type   | Required | Description |
|------------|--------|----------|-------------|
| `username` | string | no       |             |

#### TemporalResourceEntity

| Name                | Type | Required | Description |
|---------------------|------|----------|-------------|
| `startDate`         | date | no       |             |
| `endDate`           | date | no       |             |
| `durationInMinutes` | long | no       |             |

### Convert a workflowlist to a new type <a name="put_workflowlist_type"></a>

```
PUT /workflowlist/:workflowlistApiId/type
```

#### JSON body parameters

| Name             | Type             | Required | Description |
|------------------|------------------|----------|-------------|
| `newListType`    | WorkflowListType | yes      |             |

### Change the position of a workflowlist within its parent <a name="put_workflowlist_position"></a>

```
PUT /workflowlist/:workflowlistApiId/position
```

#### JSON body parameters

| Name             | Type    | Required | Description |
|------------------|---------|----------|-------------|
| `newPosition`    | integer | yes      |             |

### Move a workflowlist to a new parent <a name="put_workflowlist_parent"></a>

```
PUT /workflowlist/:workflowlistApiId/parent
```

#### JSON body parameters

| Name             | Type    | Required | Description  |
|------------------|---------|----------|--------------|
| `newParentApiId` | string  | no       |              |
| `newPosition`    | integer | no       |              |

### Get a scheduling proposal for a workflowlist from the symbolic intelligence <a name="get_workflowlist_scheduling"></a>

```
GET /workflowlist/:workflowlistApiId/scheduling
```

# Enums <a name="enums"></a>

### WorkflowListType

- `ITEM`
- `BOARD`
- `LIST`
