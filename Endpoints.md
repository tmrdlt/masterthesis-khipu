# Endpoints

### Check if server is healthy

```
GET /health
```

### Get all users

```
GET /user
```

### Create a new user

```
POST /user
```

#### Parameters

| Name       | Type   | In   | Description  |
|------------|--------|------|--------------|
| `username` | string | body | __Required__ |

### Get a single user

```
GET /user/{userApiId}
```

#### Parameters

| Name        | Type   | In   | Description  |
|-------------|--------|------|--------------|
| `userApiId` | string | path | __Required__ |

### Get all workflowlists

```
GET /workflowlist
```

#### Parameters

| Name        | Type   | In    | Description                         |
|-------------|--------|-------|-------------------------------------|
| `userApiId` | string | query | Only get of lists of specified user |

### Create a new workflowlist

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

### Delete a workflowlist

```
DELETE /workflowlist/{workflowlistId}
```

#### Parameters

| Name             | Type   | In   | Description  |
|------------------|--------|------|--------------|
| `workflowlistId` | string | path | __Required__ |

### Update a workflowlist

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

### Update the resources of a workflowlist

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

### Convert a workflowlist to a new type

```
PUT /workflowlist/{workflowlistId}/type
```

#### Parameters

| Name             | Type             | In   | Description  |
|------------------|------------------|------|--------------|
| `workflowlistId` | string           | path | __Required__ |
| `newListType`    | WorkflowListType | body | __Required__ |

### Change the position of a workflowlist within its parent

```
PUT /workflowlist/{workflowlistId}/position
```

#### Parameters

| Name             | Type    | In   | Description  |
|------------------|---------|------|--------------|
| `workflowlistId` | string  | path | __Required__ |
| `newPosition`    | integer | body | __Required__ |

### Move a workflowlist to a new parent

```
PUT /workflowlist/{workflowlistId}/parent
```

#### Parameters

| Name             | Type    | In   | Description  |
|------------------|---------|------|--------------|
| `workflowlistId` | string  | path | __Required__ |
| `newParentApiId` | string  | body |              |
| `newPosition`    | integer | body |              |

### Get a scheduling proposal for a workflowlist from the symbolic intelligence

```
GET /workflowlist/{workflowlistId}/scheduling
```

#### Parameters

| Name             | Type   | In   | Description  |
|------------------|--------|------|--------------|
| `workflowlistId` | string | path | __Required__ |

# Enums
### WorkflowListType
- `ITEM`
- `BOARD`
- `LIST`

###