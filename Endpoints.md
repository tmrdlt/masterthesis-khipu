# Table of contents

- [Authorization](#authorization)
- [Endpoints](#endpoints)
    - [`GET` health status](#get_health)
    - [`GET` all users](#get_all_users)
    - [`GET` single user](#get_single_user)
    - [`POST` user](#post_user)
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
    - [`WorkflowListType`](#workflowlisttype) 

# Authorization <a name="authorization"></a>

All requests to `/workflowlist/...` require the UUID of an active user as an `Authorization` header. Otherwise, the API will
return `401 Unauthorized`.

# Endpoints <a name="endpoints"></a>

## `GET` health status <a name="get_health"></a>
Check if the API is healthy.
```
GET /health
```

## `GET` all users <a name="get_all_users"></a>
Retrieve all users.
```
GET /user
```

## `GET` single user <a name="get_single_user"></a>

Retrieve a single user.
```
GET /user/:userApiId
```

## `POST` user <a name="post_user"></a>

Create a new user.
```
POST /user
```

#### JSON body parameters

| Name       | Type   | Required | Description |
|------------|--------|----------|-------------|
| `username` | string | yes      |             |

## `GET` all workflowlists <a name="get_all_workflowlists"></a>

Retrieve all workflow lists.
```
GET /workflowlist
```

#### URI parameters

| Name        | Type   | Required | Description    |
|-------------|--------|----------|----------------|
| `userApiId` | string | no       | Filter by user |

## `POST` workflowlist <a name="post_workflowlist"></a>

Create a new workflow list.
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

## `DELETE` workflowlist <a name="delete_workflowlist"></a>

Delete a workflow list.
```
DELETE /workflowlist/:workflowlistApiId
```

## `PUT` workflowlist <a name="put_workflowlist"></a>

Update a workflow list.
```
PUT /workflowlist/:workflowlistApiId
```

#### JSON body parameters

| Name                        | Type    | Required | Description |
|-----------------------------|---------|----------|-------------|
| `newTitle`                  | string  | yes      |             |
| `newDescription`            | string  | no       |             |
| `isTemporalConstraintBoard` | boolean | no       |             |

## `PUT` workflowlist resource <a name="put_workflowlist_resource"></a>

Update the resources of a workflow list.
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

## `PUT` workflowlist type <a name="put_workflowlist_type"></a>

Convert a workflow list to a new type.
```
PUT /workflowlist/:workflowlistApiId/type
```

#### JSON body parameters

| Name             | Type             | Required | Description |
|------------------|------------------|----------|-------------|
| `newListType`    | WorkflowListType | yes      |             |

## `PUT` workflowlist position <a name="put_workflowlist_position"></a>

Change the position of a workflow list within its parent.
```
PUT /workflowlist/:workflowlistApiId/position
```

#### JSON body parameters

| Name             | Type    | Required | Description |
|------------------|---------|----------|-------------|
| `newPosition`    | integer | yes      |             |

## `PUT` workflowlist parent <a name="put_workflowlist_parent"></a>

Move a workflow list to a new parent.
```
PUT /workflowlist/:workflowlistApiId/parent
```

#### JSON body parameters

| Name             | Type    | Required | Description  |
|------------------|---------|----------|--------------|
| `newParentApiId` | string  | no       |              |
| `newPosition`    | integer | no       |              |

## `GET` workflowlist scheduling <a name="get_workflowlist_scheduling"></a>

Retrieve a scheduling proposal for the workflow list from the symbolic intelligence.
```
GET /workflowlist/:workflowlistApiId/scheduling
```

# Enums <a name="enums"></a>

### `WorkflowListType` <a name="workflowlisttype"></a>

- `ITEM`
- `BOARD`
- `LIST`
