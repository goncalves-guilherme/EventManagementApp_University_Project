use master

if exists(select * from sys.databases where name='SilentManager')
	DROP DATABASE [SilentManager]

CREATE DATABASE [SilentManager]

use [SilentManager]

set xact_abort on
go
begin transaction

if object_id('dbo.[User]') is not null
    drop table dbo.[User]

create table dbo.[User](
	[User_Id] int Identity,
	[Name] nvarchar(25),
	[Email] varchar(25),
	[Password] varchar(125),
	[Create_Date] datetime,
	constraint pk_user primary key ([user_id]),
	constraint uc_user_email unique ([Email]),
)

if object_id('dbo.[Token]') is not null
    drop table dbo.Token

create table dbo.Token(
	[token]  nvarchar(32),
	[User_Id] int not null,
	[expiration_Date] datetime,
	constraint pk_token primary key ([token]),
	constraint fk_token_user foreign key ([User_id]) references [User],
)

if object_id('dbo.[Location]') is not null
    drop table dbo.[Location]

create table dbo.[Location](
	[Location_Id] int Identity,
	[Latitude] float,
	[Longitude] float,
	[Raio] float,
	address varchar(25),
	constraint pk_location primary key ([location_id])
)

if object_id('dbo.[Category]') is not null
    drop table dbo.[Category]

create table dbo.[Category](
	[Category_Id] int Identity,
	[Category] varchar(25),
	constraint pk_category primary key ([Category_Id]),
	constraint uc_category unique([Category])
)

insert into Category values ('Any'), ('Music'),('Food'),('Business'),('Technology'),('Art')

if object_id('dbo.[State_Event]') is not null
    drop table dbo.[State_Event]

create table dbo.[State_Event](
	[State_Event_Id] int Identity,
	[State] varchar(25),
	constraint pk_state_event primary key ([State_Event_Id]),
	constraint uc_state_event_state unique([State])
)

insert into State_Event values ('Created')

if object_id('dbo.[Event]') is not null
    drop table dbo.[Event]

create table dbo.[Event](
	[Event_Id] int Identity,
	[Name] nvarchar(25),
	[Description] nvarchar(25),
	[Start_Date] datetime,
	[End_Date] datetime,
	[Author] int,
	[Location] int,
	[State] int,
	[Radius] int,
	constraint pk_event primary key ([Event_Id]),
	constraint fk_event_utilizador foreign key ([Author]) references [User],
	constraint fk_event_localizacao foreign key ([Location]) references [Location],
	constraint fk_event_estado foreign key ([State]) references [State_Event],
	constraint check_event_data check ([Start_Date] < [End_Date]),
)

if object_id('dbo.[Event_Has_Categories]') is not null
    drop table dbo.[Event_Has_Categories]

create table dbo.[Event_Has_Categories](
	[Event_Id] int,
	[Category_Id] int,
	constraint pk_Event_Has_Categories primary key ([Event_Id], [Category_Id]),
	constraint fk_Event_Has_Categories_event_id foreign key ([Event_Id]) references [Event],
	constraint fk_Event_Has_Categories_category_id foreign key ([Category_Id]) references [Category]
)

if object_id('dbo.[Invite_State]') is not null
    drop table dbo.[Invite_State]

create table dbo.[Invite_State](
	[Invite_Id] int Identity,
	[State] varchar(15),
	constraint pk_invite_state primary key ([Invite_Id]),
	constraint uc_invite_state unique([State])
)

insert into Invite_State values('Pending'),('Accepted'),('Rejected')

if object_id('dbo.[Invite]') is not null
    drop table dbo.[Invite]

create table dbo.[Invite](
	[User_Id] int,
	[Event_Id] int,
	[State] int,
	constraint pk_invite primary key ([User_Id], [Event_Id]),
	constraint fk_invite_user foreign key ([User_Id]) references [User],
	constraint fk_invite_event foreign key ([Event_Id]) references [Event],
	constraint fk_invite_state foreign key ([State]) references [Invite_State],
)

commit transaction
go